package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")  // "order"는 SQL 예약어라 테이블명 별도 지정
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;      // 주문한 고객명 (또는 ID)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;       // 주문 상태 (예: CREATED, SHIPPED)

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL)
    private List<OrderLine> orderLines = new ArrayList<>();

    public Order(String customerName) {
        this.customerName = customerName;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }

    // 주문에 OrderLine 추가하는 편의 메서드
    public void addOrderLine(OrderLine line) {
        orderLines.add(line);
        line.setOrder(this);
    }
}

