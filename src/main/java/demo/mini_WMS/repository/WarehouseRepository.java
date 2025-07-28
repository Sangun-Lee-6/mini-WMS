package demo.mini_WMS.repository;


import demo.mini_WMS.domain.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Warehouse warehouse) {
        em.persist(warehouse);
    }

    public Optional<Warehouse> findById(Long id) {
        return Optional.ofNullable(em.find(Warehouse.class, id));
    }

    public List<Warehouse> findAll() {
        return em.createQuery("SELECT w FROM Warehouse w", Warehouse.class).getResultList();
    }
}
