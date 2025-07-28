package demo.mini_WMS.controller;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.domain.WarehouseLocation;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.WarehouseLocationRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final WarehouseLocationRepository locationRepo;
    private final InventoryRepository inventoryRepo;
    private final WarehouseRepository warehouseRepo;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/inbound")
    public String inboundPage(){
        return "inbound";
    }

    @GetMapping("/inventory")
    public String inventoryGrid(Model model) {
        List<Inventory> inventories = inventoryRepo.findAll();

        Map<String, List<Inventory>> grid = new HashMap<>();
        for (Inventory inv : inventories) {
            WarehouseLocation loc = inv.getLocation();
            String key = (char) ('A' + loc.getRowIdx()) + "-" + (loc.getColIdx() + 1);
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(inv);
        }

        List<String> rowLabels = List.of("A", "B", "C", "D", "E");
        model.addAttribute("rowLabels", rowLabels);
        model.addAttribute("grid", grid);

        return "inventory";
    }
}
