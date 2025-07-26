package demo.mini_WMS.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {
    private String customerName;
    private Long warehouseId;
    private List<OrderItemRequest> items;
}
