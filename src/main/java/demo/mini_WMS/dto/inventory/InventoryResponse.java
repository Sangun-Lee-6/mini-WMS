package demo.mini_WMS.dto.inventory;

import demo.mini_WMS.domain.Inventory;
import lombok.Data;

@Data
public class InventoryResponse {
    private Long id;
    private String productName;
    private String warehouseName;
    private Long quantity;

    public static InventoryResponse from(Inventory inventory) {
        InventoryResponse dto = new InventoryResponse();
        dto.setId(inventory.getId());
        dto.setProductName(inventory.getProduct().getName());
        dto.setWarehouseName(inventory.getWarehouse().getName());
        dto.setQuantity(inventory.getQuantity());
        return dto;
    }
}
