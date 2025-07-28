package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;

    // 연관관계 편의 메서드 (Order에서 사용)
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;

    public static OrderItem createOrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("상품이 null일 수 없습니다.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        OrderItem item = new OrderItem();
        item.product = product;
        item.quantity = quantity;

        return item;
    }
}
