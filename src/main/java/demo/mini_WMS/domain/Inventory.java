package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Setter
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @Builder
    public Inventory(Warehouse warehouse, Product product, Long quantity, WarehouseLocation location) {
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
        this.location = location;
    }

    public void addQuantity(Long qty) {
        this.quantity += qty;
    }

    public void reduceQuantity(Long qty) {
        this.quantity -= qty;
    }
}

