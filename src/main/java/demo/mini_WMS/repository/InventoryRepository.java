package demo.mini_WMS.repository;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.domain.WarehouseLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository {

    @PersistenceContext
    private EntityManager em;

    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            em.persist(inventory);
            return inventory; // persist는 반환값이 없음
        } else {
            return em.merge(inventory); // merge는 병합된 엔티티를 반환함
        }
    }

    public Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse, WarehouseLocation location) {
        List<Inventory> result = em.createQuery(
                        "SELECT i FROM Inventory i " +
                                "WHERE i.product = :product " +
                                "AND i.warehouse = :warehouse " +
                                "AND i.location = :location", Inventory.class)
                .setParameter("product", product)
                .setParameter("warehouse", warehouse)
                .setParameter("location", location)
                .getResultList();

        return result.stream().findFirst();
    }

    public List<Inventory> findAll() {
        return em.createQuery("SELECT i FROM Inventory i", Inventory.class).getResultList();
    }

    public List<Inventory> findByLocation(WarehouseLocation location) {
        return em.createQuery("SELECT i FROM Inventory i WHERE i.location = :location", Inventory.class)
                .setParameter("location", location)
                .getResultList();
    }

    public List<Inventory> findByProductIds(Collection<Long> productIds) {
        return em.createQuery("SELECT i FROM Inventory i WHERE i.product.id IN :productIds", Inventory.class)
                .setParameter("productIds", productIds)
                .getResultList();
    }

    @Transactional
    public void decreaseQuantity(Long productId, WarehouseLocation location, int quantity) {

        Inventory inventory = em.createQuery(
                        "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location = :location",
                        Inventory.class)
                .setParameter("productId", productId)
                .setParameter("location", location)
                .getSingleResult();

        long updatedQty = inventory.getQuantity() - quantity;
        if (updatedQty < 0) {
            throw new IllegalStateException("재고 수량이 부족합니다. productId=" + productId + ", locationId=" + location.getId());
        }

        inventory.setQuantity(updatedQty);
    }
}
