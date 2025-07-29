package demo.mini_WMS.dto.picking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PickingResult {
    private int totalItems;       // 총 아이템 개수
    private int totalDistance;    // 총 이동 거리 (칸 수)
    private int totalTime;        // 총 소요 시간 (초)
}