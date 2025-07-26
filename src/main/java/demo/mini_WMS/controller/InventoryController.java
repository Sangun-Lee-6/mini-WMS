package demo.mini_WMS.controller;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.dto.request.ReceiveRequest;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import demo.mini_WMS.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;

    // 특정 창고의 재고 목록 조회
    @GetMapping("/inventories")
    public List<Inventory> getInventories(@RequestParam Long warehouseId) {
        Warehouse wh = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid warehouseId"));
        // 단순히 warehouse 기준으로 전체 재고 조회 (레포지토리에 커스텀 메서드 구현해도 됨)
        return inventoryRepo.findAll().stream()
                .filter(inv -> inv.getWarehouse().getId().equals(warehouseId))
                .collect(Collectors.toList());
    }

    // 상품 입고 처리
    @PostMapping("/inventories/receive")
    public ResponseEntity<Inventory> receiveProduct(@RequestBody ReceiveRequest req) {
        Inventory updated = inventoryService.receiveProduct(
                req.getProductId(), req.getWarehouseId(),
                req.getQuantity(), req.getSupplier());
        return ResponseEntity.ok(updated);
    }

    // (추가로 상품 목록, 창고 목록 API 등 구현 가능)
}
