package demo.mini_WMS.domain.picking;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.dto.picking.PickingInput;
import demo.mini_WMS.domain.picking.PickingItem;
import demo.mini_WMS.dto.picking.PickingResult;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("tps")
public class TPSPickingStrategy implements PickingStrategy {

    private static final int MOVE_TIME_PER_CELL = 20;
    private static final int PICK_TIME_PER_ITEM = 5;

    /**
     * TPS 피킹 계획 생성
     */
    @Override
    public List<PickingItem> generatePickingPlan(Map<Long, Integer> productQuantities,
                                                 Map<Long, List<Inventory>> inventoryMap) {
        // 1️⃣ 후보군 모으기
        List<Inventory> candidates = new ArrayList<>();
        for (Map.Entry<Long, List<Inventory>> entry : inventoryMap.entrySet()) {
            if (productQuantities.containsKey(entry.getKey())) {
                candidates.addAll(entry.getValue());
            }
        }

        // 2️⃣ 최적 경로 계산
        List<Inventory> bestRoute = (candidates.size() <= 8)
                ? bruteForceRoute(candidates)
                : nearestNeighborRoute(candidates);

        // 3️⃣ FIFO처럼 필요한 수량만큼 PickingItem 생성
        Map<Long, Integer> remaining = new HashMap<>(productQuantities);
        List<PickingItem> plan = new ArrayList<>();

        for (Inventory inv : bestRoute) {
            long productId = inv.getProduct().getId();
            int need = remaining.getOrDefault(productId, 0);
            if (need <= 0) continue;

            int pickQty = (int) Math.min(need, inv.getQuantity());
            plan.add(new PickingItem(inv.getProduct(), inv.getLocation(), pickQty));
            remaining.put(productId, need - pickQty);
        }

        return plan;
    }

    /**
     * KPI 계산
     */
    @Override
    public PickingResult run(PickingInput input) {
        // ✅ Greedy와 동일한 방식으로 Map 구성
        Map<Long, Integer> productQuantities = new HashMap<>();
        Map<Long, List<Inventory>> inventoryMap = new HashMap<>();

        for (PickingItem item : input.getItems()) {
            long productId = item.getProduct().getId();

            // 총 필요 수량 누적
            productQuantities.put(productId,
                    productQuantities.getOrDefault(productId, 0) + item.getQuantity());

            // 재고 정보 누적
            inventoryMap.computeIfAbsent(productId, k -> new ArrayList<>())
                    .add(new Inventory(
                            null, // warehouse 정보 없음
                            item.getProduct(),
                            (long) item.getQuantity(),
                            item.getLocation()
                    ));
        }

        // TPS 경로 생성
        List<PickingItem> plan = generatePickingPlan(productQuantities, inventoryMap);

        // KPI 계산
        int totalItems = 0;
        int totalDistance = 0;
        int curR = 0, curC = 0;

        for (PickingItem pick : plan) {
            int targetR = pick.getLocation().getRowIdx();
            int targetC = pick.getLocation().getColIdx();
            totalDistance += distance(curR, curC, targetR, targetC);
            curR = targetR;
            curC = targetC;
            totalItems += pick.getQuantity();
        }

        // 귀환 거리 포함
        totalDistance += distance(curR, curC, 0, 0);

        int totalMoveTime = totalDistance * MOVE_TIME_PER_CELL;
        int totalPickTime = totalItems * PICK_TIME_PER_ITEM;
        int totalTime = totalMoveTime + totalPickTime;

        return new PickingResult( totalItems, totalDistance, totalTime);
    }

    // ---------------------------
    // 경로 최적화
    // ---------------------------
    private int distance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    private List<Inventory> bruteForceRoute(List<Inventory> list) {
        List<Inventory> bestRoute = null;
        int bestDist = Integer.MAX_VALUE;

        for (List<Inventory> route : permute(list)) {
            int dist = 0, curR = 0, curC = 0;
            for (Inventory inv : route) {
                dist += distance(curR, curC, inv.getLocation().getRowIdx(), inv.getLocation().getColIdx());
                curR = inv.getLocation().getRowIdx();
                curC = inv.getLocation().getColIdx();
            }
            dist += distance(curR, curC, 0, 0);
            if (dist < bestDist) {
                bestDist = dist;
                bestRoute = new ArrayList<>(route);
            }
        }
        return bestRoute;
    }

    private List<List<Inventory>> permute(List<Inventory> list) {
        List<List<Inventory>> result = new ArrayList<>();
        permuteHelper(list, 0, result);
        return result;
    }

    private void permuteHelper(List<Inventory> arr, int index, List<List<Inventory>> result) {
        if (index == arr.size() - 1) {
            result.add(new ArrayList<>(arr));
            return;
        }
        for (int i = index; i < arr.size(); i++) {
            Collections.swap(arr, index, i);
            permuteHelper(arr, index + 1, result);
            Collections.swap(arr, index, i);
        }
    }

    private List<Inventory> nearestNeighborRoute(List<Inventory> list) {
        List<Inventory> route = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int curR = 0, curC = 0;

        while (route.size() < list.size()) {
            int bestIdx = -1, bestDist = Integer.MAX_VALUE;
            for (int i = 0; i < list.size(); i++) {
                if (visited.contains(i)) continue;
                Inventory inv = list.get(i);
                int dist = distance(curR, curC, inv.getLocation().getRowIdx(), inv.getLocation().getColIdx());
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx = i;
                }
            }
            route.add(list.get(bestIdx));
            visited.add(bestIdx);
            curR = list.get(bestIdx).getLocation().getRowIdx();
            curC = list.get(bestIdx).getLocation().getColIdx();
        }
        return route;
    }
}
