package demo.mini_WMS.domain.picking;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.WarehouseLocation;
import demo.mini_WMS.dto.picking.PickingInput;
import demo.mini_WMS.dto.picking.PickingResult;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Greedy 피킹 전략
 * - 상품 ID에 관계없이 "현재 위치에서 가장 가까운 재고"를 우선 선택
 * - FIFO와 달리 상품 단위 순서가 보장되지 않음
 */
@Component("greedy")
public class GreedyPickingStrategy implements PickingStrategy {

    private static final int MOVE_TIME_PER_CELL = 20; // 초
    private static final int PICK_TIME_PER_ITEM = 5;   // 초

    @Override
    public List<PickingItem> generatePickingPlan(Map<Long, Integer> productQuantities, // productQuantities → {상품ID: 필요한 수량}
                                                 Map<Long, List<Inventory>> inventoryMap) // inventoryMap → {상품ID: [재고 목록]}
    {
        // 전체 재고 후보 리스트 생성
        // 모든 상품을 상품 구분 없이 하나의 후보군으로 등록
        List<InventoryCandidate> candidates = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            int neededQty = entry.getValue();

            List<Inventory> invList = inventoryMap.getOrDefault(productId, List.of()); // 현재 상품에 대한 재고 목록 가져오기
            for (Inventory inv : invList) {
                candidates.add(new InventoryCandidate(productId, inv, neededQty));
            }
        }

        // 상품 마다 피킹해야할 남은 수량 관리
        Map<Long, Integer> remainingQty = new HashMap<>(productQuantities);
        // ✅ 이건 어떤 형태?

        // Greedy 순회
        List<PickingItem> result = new ArrayList<>();
        // 출발점 : (0,0)
        int currentRow = 0;
        int currentCol = 0;

        while (!remainingQty.isEmpty()) {
            InventoryCandidate best = null;
            int minDist = Integer.MAX_VALUE;

            for (InventoryCandidate candidate : candidates) {
                int need = remainingQty.getOrDefault(candidate.productId, 0);
                if (need <= 0) continue;

                int dist = manhattanDistance(currentRow, currentCol, candidate.inv.getLocation());
                if (dist < minDist && candidate.inv.getQuantity() > 0) {
                    minDist = dist;
                    best = candidate;
                }
            }

            if (best == null) break;

            // 실제 피킹 수량
            int need = remainingQty.get(best.productId);
            int pickQty = (int) Math.min(need, best.inv.getQuantity());

            result.add(new PickingItem(best.inv.getProduct(), best.inv.getLocation(), pickQty));

            remainingQty.put(best.productId, need - pickQty);
            if (remainingQty.get(best.productId) <= 0) {
                remainingQty.remove(best.productId);
            }

            // 현재 위치 이동
            currentRow = best.inv.getLocation().getRowIdx();
            currentCol = best.inv.getLocation().getColIdx();

            // 실제 재고 감소(시뮬레이션)
            best.inv.setQuantity(best.inv.getQuantity() - pickQty);
        }

        return result;
    }

    @Override
    public PickingResult run(PickingInput input) {
        List<PickingItem> items = input.getItems();

        int totalDistance = 0;
        int totalItems = 0;

        int currentRow = 0;
        int currentCol = 0;

        // FIFO와 동일한 KPI 계산 로직 적용
        for (PickingItem item : items) {
            WarehouseLocation location = item.getLocation();

            int targetRow = location.getRowIdx();
            int targetCol = location.getColIdx();

            int distance = Math.abs(currentRow - targetRow) + Math.abs(currentCol - targetCol);
            totalDistance += distance;

            currentRow = targetRow;
            currentCol = targetCol;

            totalItems += item.getQuantity();
        }

        int totalMoveTime = totalDistance * MOVE_TIME_PER_CELL;
        int totalPickTime = totalItems * PICK_TIME_PER_ITEM;

        int returnDistance = Math.abs(currentRow) + Math.abs(currentCol);
        totalDistance += returnDistance;
        totalMoveTime += returnDistance * MOVE_TIME_PER_CELL;

        int totalTime = totalMoveTime + totalPickTime;

        return new PickingResult(totalItems, totalDistance, totalTime);
    }

    private int manhattanDistance(int r1, int c1, WarehouseLocation loc) {
        return Math.abs(r1 - loc.getRowIdx()) + Math.abs(c1 - loc.getColIdx());
    }

    private static class InventoryCandidate {
        Long productId;
        Inventory inv;
        int neededQty;

        InventoryCandidate(Long productId, Inventory inv, int neededQty) {
            this.productId = productId;
            this.inv = inv;
            this.neededQty = neededQty;
        }
    }
}
