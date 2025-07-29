package demo.mini_WMS.domain.picking;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.dto.picking.PickingInput;
import demo.mini_WMS.dto.picking.PickingResult;

import java.util.List;
import java.util.Map;

public interface PickingStrategy {
    List<PickingItem> generatePickingPlan(Map<Long, Integer> productQuantities, Map<Long, List<Inventory>> inventoryMap);

    PickingResult run(PickingInput input);  // KPI 계산용 (총이동거리, 시간 등)
}