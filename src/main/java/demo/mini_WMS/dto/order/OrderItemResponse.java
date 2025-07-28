package demo.mini_WMS.dto.order;

import demo.mini_WMS.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private final Long productId;
    private final String productName;
    private final int quantity;

    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getName(),
                item.getQuantity()
        );
    }
}