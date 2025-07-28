package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class WarehouseLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rowIdx; // 예: 0~4
    private int colIdx; // 예: 0~9

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private int capacity = 5; // 한 위치당 최대 저장 가능 수량

    @Builder
    public WarehouseLocation(Warehouse warehouse, int rowIdx, int colIdx) {
        this.warehouse = warehouse;
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
    }

    public String getCode() {
        return String.format("%c-%02d", 'A' + rowIdx, colIdx + 1);
    }
}