package demo.mini_WMS.service;

import demo.mini_WMS.domain.*;
import demo.mini_WMS.domain.picking.Picking;
import demo.mini_WMS.domain.picking.PickingItem;
import demo.mini_WMS.domain.picking.PickingStrategy;
import demo.mini_WMS.dto.picking.PickingInput;
import demo.mini_WMS.dto.picking.PickingResult;
import demo.mini_WMS.dto.picking.PickingResultResponse;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.OrderRepository;
import demo.mini_WMS.repository.PickingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PickingService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final PickingRepository pickingRepository;

    private final PickingStrategy pickingStrategy; // 인터페이스 기반 알고리즘 전략 주입

    /**
     * 피킹 전체 프로세스 실행 (주문 취합 → 피킹 → KPI 기록)
     */
    public PickingResultResponse executePicking(String algorithmName) {
        // 1. 주문 목록에서 피킹 대상 상품 총 수량 취합
        Map<Long, Integer> totalProductQuantities = aggregateProductQuantitiesFromWaitingOrders();

        if (totalProductQuantities.isEmpty()) {
            throw new IllegalStateException("피킹할 상품이 없습니다. (WAITING 상태 주문이 없음)");
        }

        // 2. Picking 엔티티 생성(피킹 세션)
        Picking picking = new Picking(algorithmName);

        // 3. 피킹 아이템 생성 (재고 위치 기반)
        //피킹 대상의 보관 위치와 수량 조회
        List<Inventory> inventories = inventoryRepository.findByProductIds(totalProductQuantities.keySet()); // keySet : 상품ID 목록
        //{ "상품ID" : [재고 목록] }
        Map<Long, List<Inventory>> inventoryMap = mapInventoriesByProductId(inventories);

        /*
        System.out.println("=== [DEBUG] inventoryMap 구성 ===");
        for (Map.Entry<Long, List<Inventory>> entry : inventoryMap.entrySet()) {
            Long productId = entry.getKey();
            List<Inventory> invList = entry.getValue();

            System.out.println("상품ID: " + productId);
            for (Inventory inv : invList) {
                System.out.println("  - 위치: " + inv.getLocation().getCode() +
                        ", 수량: " + inv.getQuantity());
            }
        }
        */

        // 4. 피킹 전략을 이용해 PickingItem 생성
        List<PickingItem> pickingItems = pickingStrategy.generatePickingPlan(totalProductQuantities, inventoryMap);

        /*
          System.out.println("=== [DEBUG] 피킹 계획 상세 ===");
          for (PickingItem item : pickingItems) {
          System.out.printf("상품: %s | 위치: %s | 수량: %d\n",
                    item.getProduct().getName(),
                    item.getLocation().getCode(),
                    item.getQuantity());
        }
         */



        // 5. Picking 객체에 피킹 아이템 추가
        for (PickingItem item : pickingItems) {
            picking.addItem(item);
        }

        // 6. 피킹 저장 (Cascade로 PickingItem도 함께 저장)
        pickingRepository.save(picking);

        // 7. KPI 계산 (전략에 따라 다름)
        PickingInput input = new PickingInput(picking.getItems());
        PickingResult result = pickingStrategy.run(input);

        // 8. KPI 기록
        picking.recordKPI(result.getTotalItems(), result.getTotalDistance(), result.getTotalTime());

        // 9. 주문 상태 PACKING 으로 변경
        int updatedCount = orderRepository.updateStatusByStatus(OrderStatus.WAITING, OrderStatus.PICKED);
        if (updatedCount == 0) {
            throw new IllegalStateException("WAITING 상태의 주문이 존재하지 않아 상태를 변경할 수 없습니다.");
        }

        // 10. 재고 차감
        for (PickingItem item : picking.getItems()) {
            inventoryRepository.decreaseQuantity(item.getProduct().getId(), item.getLocation(), item.getQuantity());
        }

        List<PickingResultResponse.PickingItemDto> itemDtos = picking.getItems().stream()
                .map(item -> new PickingResultResponse.PickingItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getLocation().getId(),
                        item.getLocation().getCode(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList()); // ← toList() 대신

        return PickingResultResponse.builder()
                .algorithm(picking.getAlgorithm())
                .totalItems(result.getTotalItems())
                .totalDistance(result.getTotalDistance())
                .totalTime(result.getTotalTime())
                .items(itemDtos)
                .build();
    }

    /**
     * 주문 중 WAITING 상태인 것들의 OrderItem을 모아 상품별 수량 집계
     * { "productId" : "상품 수" } 형태의 Map을 반환
     */
    private Map<Long, Integer> aggregateProductQuantitiesFromWaitingOrders() {
        List<Orders> waitingOrders = orderRepository.findByStatus(OrderStatus.WAITING);
        Map<Long, Integer> result = new HashMap<>();
        for (Orders order : waitingOrders) {
            for (OrderItem item : order.getItems()) {
                Long productId = item.getProduct().getId();
                int qty = item.getQuantity();
                result.put(productId, result.getOrDefault(productId, 0) + qty);
            }
        }
        return result;
    }

    /**
     * 상품별로 재고 위치를 그룹핑한 후,
     * 각 상품의 재고 리스트를 "멘해튼 거리" 기준으로 정렬한다.
     *
     * [멘해튼 거리(Manhattan Distance)]
     * - 격자형 창고에서 작업자가 이동할 때,
     *   대각선이 아닌 상하좌우(행(row), 열(col))로만 이동할 수 있다고 가정한 거리
     * - 계산식: distance = |현재행 - 목표행| + |현재열 - 목표열|
     * - 여기서는 기준점을 (0,0)으로 가정하여 rowIdx + colIdx 값이 작을수록
     *   "가까운 위치"로 간주하여 정렬한다.
     */
    Map<Long, List<Inventory>> mapInventoriesByProductId(List<Inventory> inventories) {
        Map<Long, List<Inventory>> map = new HashMap<>(); //피킹 계획을 담을 Map, {"상품" : [재고 1, 재고 2...]}
        // 상품별로 묶기
        for (Inventory inv : inventories) {
            Long productId = inv.getProduct().getId();
            map.computeIfAbsent(productId, k -> new ArrayList<>()).add(inv);
        }

        // 각 상품의 재고 리스트를 (rowIdx + colIdx) 기준으로 정렬
        //     → 멘해튼 거리상 0,0에 가까운 위치부터 선택 가능하도록
        for (List<Inventory> invList : map.values()) {
            invList.sort(Comparator.comparingInt(inv -> {
                WarehouseLocation loc = inv.getLocation();
                return loc.getRowIdx() + loc.getColIdx(); // 가장 가까운 곳부터
            }));
        }

        return map;
    }

}
