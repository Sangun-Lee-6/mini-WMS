package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InboundReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name="warehouse_id")
    private Warehouse warehouse;

    private Long quantity;          // 입고 수량
    private String supplier;        // 공급자 정보 (옵션)

    @Column(updatable = false)
    private LocalDateTime receivedAt;   // 입고 시각

    public InboundReceipt(Product product, Warehouse warehouse, Long quantity, String supplier) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = quantity;
        this.supplier = supplier;
        this.receivedAt = LocalDateTime.now();
    }
}

