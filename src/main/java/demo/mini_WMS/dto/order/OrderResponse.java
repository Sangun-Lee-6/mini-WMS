package demo.mini_WMS.dto.order;

import demo.mini_WMS.domain.Orders;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long orderId;
    private String createdAt;
    private List<OrderItemResponse> items;

    public static OrderResponse fromEntity(Orders order) {
        OrderResponse dto = new OrderResponse();
        dto.orderId = order.getId();
        dto.createdAt = String.valueOf(order.getCreatedAt());
        dto.items = order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());
        return dto;
    }
}