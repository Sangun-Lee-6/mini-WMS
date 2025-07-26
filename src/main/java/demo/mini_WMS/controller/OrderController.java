package demo.mini_WMS.controller;

import demo.mini_WMS.domain.Order;
import demo.mini_WMS.dto.request.OrderCreateRequest;
import demo.mini_WMS.repository.OrderRepository;
import demo.mini_WMS.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepo;

    // 주문 생성
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreateRequest req) {
        Order newOrder = orderService.createOrder(
                req.getCustomerName(),
                req.getWarehouseId(),
                req.getItems()  // List<OrderItemRequest>
        );
        return ResponseEntity.ok(newOrder);
    }

    // 주문 목록 조회
    @GetMapping("/orders")
    public List<Order> listOrders() {
        return orderRepo.findAll();
    }

    // 주문 상세 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return orderRepo.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 주문 출하 처리 (상태 변경)
    @PostMapping("/orders/{orderId}/ship")
    public ResponseEntity<Order> shipOrder(@PathVariable Long orderId) {
        Order shipped = orderService.shipOrder(orderId);
        return ResponseEntity.ok(shipped);
    }
}
