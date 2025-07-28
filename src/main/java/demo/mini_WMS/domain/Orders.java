package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();
    // 연관관계 비주인이고, 부모 역할인 Order에 cascade를 걸어줌
    // order 객체가 처리되면, 관련된 orderItem 객체들도 같이 처리됨

    public static Orders createOrder(List<OrderItem> items) {
        Orders orders = new Orders(); // order 객체 생성
        orders.createdAt = LocalDateTime.now(); // 생성시각 설정
        orders.setStatus(OrderStatus.WAITING); // 생성 시 waiting 상태(피킹 전)
        // 매개변수인 아이템들을 주문에 넣음(주문 하나당 여러 개 아이템을 주문할 수 있으니까)
        for (OrderItem item : items) {
            item.setOrders(orders); // 아이템에 주문 정보 추가
            orders.getItems().add(item); // 주문 객체에도 아이템 정보 추가
        }
        return orders;
    }
}

