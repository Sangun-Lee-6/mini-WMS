package demo.mini_WMS.repository;

import demo.mini_WMS.domain.Inventory;
import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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

    public Optional<Inventory> findById(Long id) {
        return Optional.ofNullable(em.find(Inventory.class, id));
    }

    public Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse) {
        List<Inventory> result = em.createQuery(
                        "SELECT i FROM Inventory i WHERE i.product = :product AND i.warehouse = :warehouse", Inventory.class)
                .setParameter("product", product)
                .setParameter("warehouse", warehouse)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<Inventory> findByWarehouse(Warehouse warehouse) {
        return em.createQuery("SELECT i FROM Inventory i WHERE i.warehouse = :warehouse", Inventory.class)
                .setParameter("warehouse", warehouse)
                .getResultList();
    }

    public List<Inventory> findAll() {
        return em.createQuery("SELECT i FROM Inventory i", Inventory.class).getResultList();
    }
}
