package demo.mini_WMS.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {
    private Long productId;
    private Long warehouseId;
    private Long quantity;
    private String supplier;
}
