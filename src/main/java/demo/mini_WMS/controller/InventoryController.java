package demo.mini_WMS.controller;

import demo.mini_WMS.dto.inventory.InventoryRequest;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import demo.mini_WMS.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;

    @PostMapping("/receive")
    public String receiveProduct(@RequestBody InventoryRequest request) {
        inventoryService.receiveProduct(
                request.getProductId(),
                request.getWarehouseId(),
                request.getQuantity(),
                request.getSupplier()
        );
        return "입고 완료";
    }

    @PostMapping("/release")
    public String releaseProduct(@RequestBody InventoryRequest request) {
        inventoryService.releaseProduct(
                request.getProductId(),
                request.getWarehouseId(),
                request.getQuantity()
        );
        return "출고 완료";
    }

    // (추가로 상품 목록, 창고 목록 API 등 구현 가능)
}
