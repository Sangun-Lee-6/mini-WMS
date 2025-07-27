package demo.mini_WMS.controller;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import demo.mini_WMS.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class InventoryViewController {
    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryService inventoryService;

    @GetMapping("/inventory")
    public String inventoryView(@RequestParam(name = "warehouseId", required = false) Long warehouseId, Model model)
    {
        // warehouseId 파라미터 없으면 기본으로 1번 창고 사용 (예시)
        Long wid = (warehouseId != null ? warehouseId : 1L);
        Warehouse wh = warehouseRepo.findById(wid)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        List<Inventory> list = inventoryRepo.findAll().stream()
                .filter(inv -> inv.getWarehouse().equals(wh))
                .collect(Collectors.toList());
        model.addAttribute("inventoryList", list);
        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("warehouse", wh);
        return "inventory";  // inventory.html 뷰 템플릿 반환
    }
}