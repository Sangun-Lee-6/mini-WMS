package demo.mini_WMS.domain.picking;


import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.WarehouseLocation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PickingItem { // 피킹 항목

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 피킹 세션에 속한 항목인지
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picking_id")
    private Picking picking;

    // 어떤 상품을
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 어디에서
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    // 몇 개 피킹했는지
    private int quantity;

    public PickingItem(Product product, WarehouseLocation location, int quantity) {
        this.product = product;
        this.location = location;
        this.quantity = quantity;
    }
}