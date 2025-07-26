package demo.mini_WMS.service;

import demo.mini_WMS.domain.InboundReceipt;
import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.repository.InboundReceiptRepository;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // Lombok: final 필드에 대한 생성자 주입
public class InventoryService {
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryRepository inventoryRepo;
    private final InboundReceiptRepository inboundRepo;

    @Transactional
    public Inventory receiveProduct(Long productId, Long warehouseId, long quantity, String supplier) {
        // 1. 해당 상품 및 창고 엔티티 조회 (없으면 예외 발생)
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid warehouse ID"));

        // 2. 재고 엔티티 조회 또는 새로 생성
        Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                .orElse(new Inventory(product, warehouse, 0L));

        // 3. 재고 증가 및 저장
        inventory.addQuantity(quantity);
        Inventory savedInventory = inventoryRepo.save(inventory);

        // 4. 입고 이력 기록 저장
        InboundReceipt receipt = new InboundReceipt(product, warehouse, quantity, supplier);
        inboundRepo.save(receipt);

        return savedInventory;
    }

    @Transactional
    public void releaseProduct(Long productId, Long warehouseId, Long quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product"));
        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid warehouse"));
        Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() -> new IllegalArgumentException("No inventory found"));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock to release");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        // 필요시 출고 기록 저장 (OutboundReceipt 등)
    }
}
