package demo.mini_WMS.service;

import demo.mini_WMS.domain.*;
import demo.mini_WMS.dto.request.OrderItemRequest;
import demo.mini_WMS.repository.InventoryRepository;
import demo.mini_WMS.repository.OrderRepository;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryRepository inventoryRepo;
    private final OrderRepository orderRepo;

    @Transactional
    public Order createOrder(String customerName, Long warehouseId, List<OrderItemRequest> items) {
        // 1. Order 엔티티 생성
        Order order = new Order(customerName);

        // 2. 각 주문 상품 항목 처리
        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid warehouse ID"));
        for (OrderItemRequest req : items) {
            Product product = productRepo.findById(req.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID " + req.getProductId()));
            // 해당 상품의 재고 확보
            Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                    .orElseThrow(() -> new IllegalStateException("상품 재고가 없습니다: " + product.getName()));
            if (inventory.getQuantity() < req.getQuantity()) {
                throw new IllegalStateException("재고 부족: " + product.getName());
            }
            // 재고 차감
            inventory.subtractQuantity(req.getQuantity());
            inventoryRepo.save(inventory);
            // OrderLine 생성하여 Order에 추가
            OrderLine line = new OrderLine(product, req.getQuantity());
            order.addOrderLine(line);
        }
        // 3. 주문 저장 (Order와 OrderLine들이 cascade로 함께 저장)
        Order savedOrder = orderRepo.save(order);
        return savedOrder;
    }

    @Transactional
    public Order shipOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문이 존재하지 않습니다."));
        order.setStatus(OrderStatus.SHIPPED);
        // (이미 재고 차감은 주문 생성 시 했다고 가정)
        return order;  // 변경사항은 트랜잭션 커밋 시 자동 반영 (더티체킹)
    }
}
