package demo.mini_WMS.domain.picking;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Picking { // 피킹 세션(작업 단위)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가
    private Long id;

    private LocalDateTime startedAt; // 피킹 작업 시작 시간

    //KPI 지표
    private int totalItemsPicked; // 총 피킹한 물품 수
    private int totalDistanceMoved; // 총 이동 거리(칸 수)
    private int totalTimeSeconds; // 총 소요 시간(초 단위)

    private String algorithm; // 피킹 알고리즘

    @OneToMany(mappedBy = "picking", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // PickingItem.picking이 연관관계 주인
    private List<PickingItem> items = new ArrayList<>(); // 피킹 항목 리스트

    // 생성자 : 피킹 시작 시간과 알고리즘을 세팅
    public Picking(String algorithm) {
        this.startedAt = LocalDateTime.now();
        this.algorithm = algorithm;
    }

    // 양방향 연관관계 편의 메서드
    public void addItem(PickingItem item) {
        items.add(item);
        item.setPicking(this);
    }

    // 피킹 작업이 끝난 후 KPI 기록
    public void recordKPI(int totalItems, int distance, int time) {
        this.totalItemsPicked = totalItems;
        this.totalDistanceMoved = distance;
        this.totalTimeSeconds = time;
    }
}