package demo.mini_WMS.controller;

import demo.mini_WMS.dto.order.OrderResponse;
import demo.mini_WMS.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/random")
    public OrderResponse createRandomOrder() {
        return orderService.createRandomOrder();
    }

    @GetMapping("/waiting")
    public List<OrderResponse> getWaitingOrders() {
        return orderService.getWaitingOrders();
    }
}
