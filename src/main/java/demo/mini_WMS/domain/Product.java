package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter // TODO : Setter 제거하기
@NoArgsConstructor
public class Product { // 상품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String skuCode; // 상품 코드

    @Column(nullable = false)
    private String name; // 상품명

    private String category; // 상품 분류

    public Product(String skuCode, String name, String category) {
        this.skuCode = skuCode;
        this.name = name;
        this.category = category;
    }
}
