package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 창고 이름

    private String location; // 창고 위치 정보

    public Warehouse(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
