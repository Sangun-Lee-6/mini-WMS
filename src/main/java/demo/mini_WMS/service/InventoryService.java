package demo.mini_WMS.service;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.domain.WarehouseLocation;
import demo.mini_WMS.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor  // Lombok: final 필드에 대한 생성자 주입
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final WarehouseLocationRepository locationRepository;

    /**
     * 창고 위치 랜덤 할당
     */
    @Transactional
    public WarehouseLocation assignAvailableLocation(Warehouse warehouse, Product product) {
        List<WarehouseLocation> locations = locationRepository.findByWarehouse(warehouse);

        List<WarehouseLocation> availableLocations = locations.stream()
                .filter(loc -> {
                    List<Inventory> inventories = inventoryRepository.findByLocation(loc);

                    // 1. 완전히 비어있는 경우
                    if (inventories.isEmpty()) return true;

                    // 2. 이미 해당 상품만 있고 수량이 capacity 미만인 경우
                    if (inventories.size() == 1) {
                        Inventory inv = inventories.get(0);
                        return inv.getProduct().equals(product) && inv.getQuantity() < loc.getCapacity();
                    }

                    return false;
                })
                .toList();

        if (availableLocations.isEmpty()) {
            throw new IllegalStateException("모든 위치가 가득 찼거나 상품 종류 제한에 걸렸습니다.");
        }

        int randomIndex = new Random().nextInt(availableLocations.size());
        return availableLocations.get(randomIndex);
    }

}
