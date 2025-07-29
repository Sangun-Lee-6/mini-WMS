package demo.mini_WMS.service;

import demo.mini_WMS.domain.*;
import demo.mini_WMS.domain.inbound.Inbound;
import demo.mini_WMS.domain.inbound.InboundItem;
import demo.mini_WMS.dto.inbound.InboundCreateRequest;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseLocationRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import demo.mini_WMS.repository.inbound.InboundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboundService {

    private final InboundRepository inboundRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseLocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    @Transactional
    public Inbound createInbound(InboundCreateRequest request) {
        //창고 정보 조회
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid warehouse"));

        //입고 마스터 데이터 생성
        Inbound inbound = Inbound.builder()
                .warehouse(warehouse)
                .supplier(request.getSupplier())
                .inboundDate(LocalDateTime.now())
                .build();

        //입고된 항목 리스트 처리(상품ID + 수량)
        for (InboundCreateRequest.Item itemReq : request.getItems()) {
            //요청 상품ID 조회
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product"));
            //입고 아이템 생성(상품, 수량 정보)
            InboundItem item = InboundItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .build();
            //입고 마스터 데이터에 입고 아이템 추가
            inbound.addItem(item);

            //비어있는 창고 위치 할당
            int remainingQty = Math.toIntExact(itemReq.getQuantity());
            while (remainingQty > 0) {
                WarehouseLocation location = inventoryService.assignAvailableLocation(warehouse, product);
                long existingQty = inventoryRepository
                        .findByProductAndWarehouse(product, warehouse, location)
                        .map(Inventory::getQuantity)
                        .orElse(0L);
                long availableSpace = location.getCapacity() - existingQty;
                long putQty = Math.min(availableSpace, remainingQty);

                // 동일 조건으로 Inventory 찾거나 새로 생성
                Inventory inventory = inventoryRepository
                        .findByProductAndWarehouse(product, warehouse, location)
                        .orElseGet(() -> Inventory.builder()
                                .warehouse(warehouse)
                                .product(product)
                                .location(location)
                                .quantity(0L)
                                .build()
                        );

                inventory.addQuantity(putQty);
                inventoryRepository.save(inventory);

                remainingQty -= (int) putQty;
            }
        }

        // 입고 마스터 저장 후 반환
        return inboundRepository.save(inbound);
    }

    @Transactional(readOnly = true)
    public List<Inbound> getInboundList(Long warehouseId) {
        return inboundRepository.findByWarehouseIdOrderByInboundDateDesc(warehouseId);
    }
}
