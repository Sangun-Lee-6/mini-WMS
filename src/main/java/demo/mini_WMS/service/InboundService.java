package demo.mini_WMS.service;

import demo.mini_WMS.domain.*;
import demo.mini_WMS.dto.inbound.InboundCreateRequest;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import demo.mini_WMS.repository.inbound.InboundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InboundService {

    private final InboundRepository inboundRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

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
        for (InboundCreateRequest.Item itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product"));

            InboundItem item = InboundItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .build();

            inbound.addItem(item);

            Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                    .orElseGet(() -> Inventory.builder()
                            .warehouse(warehouse)
                            .product(product)
                            .quantity(0L)
                            .build()
                    );

            inventory.addQuantity(itemReq.getQuantity());
            inventoryRepository.save(inventory);
        }

        // 입고 마스터 저장 후 반환
        return inboundRepository.save(inbound);
    }

    @Transactional(readOnly = true)
    public List<Inbound> getInboundList(Long warehouseId) {
        return inboundRepository.findByWarehouseIdOrderByInboundDateDesc(warehouseId);
    }

    @Transactional(readOnly = true)
    public Inbound getInboundDetail(Long inboundId) {
        return inboundRepository.findById(inboundId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid inbound"));
    }
}
