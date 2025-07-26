package demo.mini_WMS.repository;


import demo.mini_WMS.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public Order save(Order order) {
        if (order.getId() == null) {
            em.persist(order);
            return order; // persist 후 원본 반환
        } else {
            return em.merge(order); // merge는 병합된 객체 반환
        }
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    public void delete(Order order) {
        em.remove(em.contains(order) ? order : em.merge(order));
    }
}

