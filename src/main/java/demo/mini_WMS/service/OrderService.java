package demo.mini_WMS.service;

import demo.mini_WMS.domain.Orders;
import demo.mini_WMS.domain.OrderItem;
import demo.mini_WMS.domain.Product;
import demo.mini_WMS.dto.order.OrderItemResponse;
import demo.mini_WMS.dto.order.OrderResponse;
import demo.mini_WMS.repository.OrderRepository;
import demo.mini_WMS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository; // 상품 목록 조회를 위해
    private final OrderRepository orderRepository; // 생성된 주문 저장을 위해

    private final Random random = new Random(); // 랜덤 상품 선택을 위해

    @Transactional
    public OrderResponse createRandomOrder() {
        //등록된 전체 상품 조회
        List<Product> allProducts = productRepository.findAll();

        if (allProducts.isEmpty()) {
            throw new IllegalStateException("등록된 상품이 없습니다.");
        }

        int itemCount = random.nextInt(3) + 1; // 1~3개 랜덤 상품 수
        List<OrderItem> orderItems = new ArrayList<>(); // 아이템 리스트 초기화

        // 각 상품에 대해 주문할 상품 및 수량 생성
        for (int i = 0; i < itemCount; i++) {
            Product product = allProducts.get(random.nextInt(allProducts.size()));
            int quantity = random.nextInt(5) + 1; // 수량 1~5개
            orderItems.add(OrderItem.createOrderItem(product, quantity));
        }

        // 주문 생성
        Orders orders = Orders.createOrder(orderItems);
        orderRepository.save(orders); // DB 저장(orderItem도 같이 저장)

        // DTO 변환
        OrderResponse dto = new OrderResponse();
        dto.setOrderId(orders.getId());
        dto.setCreatedAt(orders.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        dto.setItems(
                orders.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public List<OrderResponse> getWaitingOrders() {
        List<Orders> waitingOrders = orderRepository.findWaitingOrders();
        return waitingOrders.stream().map(OrderResponse::fromEntity).toList();
    }
}