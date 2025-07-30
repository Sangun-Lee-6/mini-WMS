package demo.mini_WMS.domain.picking;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.WarehouseLocation;
import demo.mini_WMS.dto.picking.PickingInput;
import demo.mini_WMS.dto.picking.PickingResult;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("tsp")
public class TspPickingStrategy implements PickingStrategy {

    private static final int MOVE_TIME_PER_CELL = 20; // 초
    private static final int PICK_TIME_PER_ITEM = 5;   // 초

    /**
     * 1단계: Greedy 방식과 동일하게 PickingPlan을 생성
     * - 입력 순서를 정렬하여 결과를 결정적으로 만듦
     */
    @Override
    public List<PickingItem> generatePickingPlan(Map<Long, Integer> totalProductQuantities,
                                                 Map<Long, List<Inventory>> inventoryMap) {
        List<PickingItem> plan = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : totalProductQuantities.entrySet()) {
            Long productId = entry.getKey();
            int requiredQty = entry.getValue();

            List<Inventory> stocks = inventoryMap.get(productId);
            if (stocks == null) continue;

            for (Inventory inv : stocks) {
                if (requiredQty <= 0) break;

                int stockQty = inv.getQuantity() != null ? inv.getQuantity().intValue() : 0;
                if (stockQty <= 0) continue; // 재고가 없는 경우 건너뜀

                int pickQty = Math.min(requiredQty, stockQty);

                // 유효한 피킹 수량만 추가
                if (pickQty > 0) {
                    plan.add(new PickingItem(inv.getProduct(), inv.getLocation(), pickQty));
                    requiredQty -= pickQty;
                }
            }
        }

        return plan;
    }


    /**
     * 2단계: DP 기반 TSP 실행
     */
    @Override
    public PickingResult run(PickingInput input) {
        List<PickingItem> items = input.getItems();

        // 1️⃣ 총 아이템 수
        int totalItems = items.stream().mapToInt(PickingItem::getQuantity).sum();

        // 2️⃣ 위치 리스트 구성 (출발점 (0,0))
        List<WarehouseLocation> locations = new ArrayList<>();
        locations.add(createStartLocation());
        for (PickingItem item : items) {
            locations.add(item.getLocation());
        }
        int n = locations.size();

        // 3️⃣ 거리 행렬 생성
        int[][] dist = buildDistanceMatrix(locations);

        // 4️⃣ 최단 경로 거리 계산 (TSP)
        int totalDistance = solveTSP(dist, n);

        // 5️⃣ KPI 계산
        int totalMoveTime = totalDistance * 20; // 이동칸수 * 20초
        int totalPickTime = totalItems * 5;     // 피킹아이템수 * 5초
        int totalTime = totalMoveTime + totalPickTime;

        // 6️⃣ 결과 반환
        return new PickingResult(totalItems, totalDistance, totalTime);
    }


    /**
     * 출발지 (0,0) 더미 위치 생성
     */
    private WarehouseLocation createStartLocation() {
        return WarehouseLocation.builder()
                .warehouse(null)
                .rowIdx(0)
                .colIdx(0)
                .build();
    }

    /**
     * 거리 행렬 생성
     */
    private int[][] buildDistanceMatrix(List<WarehouseLocation> locations) {
        int n = locations.size();
        int[][] dist = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) dist[i][j] = 0;
                else dist[i][j] = manhattanDistance(
                        locations.get(i).getRowIdx(), locations.get(i).getColIdx(),
                        locations.get(j).getRowIdx(), locations.get(j).getColIdx()
                );
            }
        }
        return dist;
    }

    /**
     * 맨해튼 거리 계산
     */
    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * DP 기반 TSP Solver
     * - 동일 비용 경로가 존재할 경우 index가 작은 v를 우선 선택 → 결과 결정적 보장
     */
    private int solveTSP(int[][] dist, int n) {
        int maxState = 1 << n;
        int[][] dp = new int[maxState][n];

        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);

        // 시작점(0)에서 출발
        dp[1][0] = 0;

        for (int mask = 1; mask < maxState; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0) continue;
                if (dp[mask][u] == Integer.MAX_VALUE / 2) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue;
                    int nextMask = mask | (1 << v);
                    int newCost = dp[mask][u] + dist[u][v];

                    // tie-breaking
                    if (newCost < dp[nextMask][v] ||
                            (newCost == dp[nextMask][v] && v < u)) {
                        dp[nextMask][v] = newCost;
                    }
                }
            }
        }

        // 모든 노드 방문 후 최소 거리 (출발점으로 돌아가지 않음)
        int res = Integer.MAX_VALUE;
        int fullMask = maxState - 1;
        for (int u = 0; u < n; u++) {
            res = Math.min(res, dp[fullMask][u]);
        }

        return res;
    }
}