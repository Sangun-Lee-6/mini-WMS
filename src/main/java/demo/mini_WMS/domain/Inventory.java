package demo.mini_WMS.domain;
//
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@Table(uniqueConstraints = {
//        @UniqueConstraint(name="uq_product_warehouse", columnNames={"product_id","warehouse_id"})
//})
//public class Inventory {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(optional = false)        // 각 재고항목은 하나의 상품에 대응
//    @JoinColumn(name="product_id")
//    private Product product;
//
//    @ManyToOne(optional = false)        // 하나의 창고에 존재
//    @JoinColumn(name="warehouse_id")
//    private Warehouse warehouse;
//
//    @Column(nullable = false)
//    private Long quantity;             // 해당 상품 재고 수량
//
//    // 편의 메서드
//    public void addQuantity(long qty) {
//        this.quantity += qty;
//    }
//    public void subtractQuantity(long qty) {
//        this.quantity -= qty;
//    }
//
//    @Builder
//    public Inventory(Product product, Warehouse warehouse, Long quantity) {
//        this.product = product;
//        this.warehouse = warehouse;
//        this.quantity = quantity;
//    }
//}


import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}

