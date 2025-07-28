package demo.mini_WMS.repository;

import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.domain.WarehouseLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseLocationRepository {

    @PersistenceContext
    private EntityManager em;

    public WarehouseLocation save(WarehouseLocation location) {
        if (location.getId() == null) {
            em.persist(location);
            return location;
        } else {
            return em.merge(location);
        }
    }

    public List<WarehouseLocation> findByWarehouse(Warehouse warehouse) {
        return em.createQuery("SELECT wl FROM WarehouseLocation wl WHERE wl.warehouse = :warehouse", WarehouseLocation.class)
                .setParameter("warehouse", warehouse)
                .getResultList();
    }

}
