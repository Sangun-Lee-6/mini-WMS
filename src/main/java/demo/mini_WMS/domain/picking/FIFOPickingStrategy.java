package demo.mini_WMS.domain.picking;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.WarehouseLocation;
import demo.mini_WMS.dto.picking.PickingInput;
import demo.mini_WMS.dto.picking.PickingResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FIFOPickingStrategy implements PickingStrategy{
    @Override
    public List<PickingItem> generatePickingPlan(Map<Long, Integer> productQuantities, Map<Long, List<Inventory>> inventoryMap) {
        List<PickingItem> result = new ArrayList<>();

        // 피킹 대상 상품의 수량을 기준으로 각 재고에서 순차적으로 피킹할 항목 생성
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey(); // 현재 피킹할 상품ID
            int remainingQty = entry.getValue(); // 피킹해야할 남은 수

            List<Inventory> locations = inventoryMap.getOrDefault(productId, List.of());
            for (Inventory inv : locations) {
                if (remainingQty <= 0) break; // 피킹 수량이 0이면 더이상 피킹 안해도 됨
                int pickQty = (int) Math.min(inv.getQuantity(), remainingQty); // 현재 재고 수량과 남은 필요 수량 중 더 적은 값 선택
                //피킹 항목 데이터 생성 : 어떤 상품을 어디서 몇 개 피킹했는지
                result.add(new PickingItem(inv.getProduct(), inv.getLocation(), pickQty));
                remainingQty -= pickQty;
            }
        }

        return result;
    }

    private static final int MOVE_TIME_PER_CELL = 20; // 초
    private static final int PICK_TIME_PER_ITEM = 5;  // 초

    @Override
    public PickingResult run(PickingInput input) {
        List<PickingItem> items = input.getItems();

        int totalDistance = 0;
        int totalItems = 0;

        // 시작 위치 (0, 0)에서 출발
        int currentRow = 0;
        int currentCol = 0;

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
        int totalTime = totalMoveTime + totalPickTime;

        return new PickingResult(totalItems, totalDistance, totalTime);
    }
}
