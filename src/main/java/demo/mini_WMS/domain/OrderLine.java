package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne @JoinColumn(name="product_id")
    private Product product;

    private Long quantity;

    public OrderLine(Product product, Long quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
