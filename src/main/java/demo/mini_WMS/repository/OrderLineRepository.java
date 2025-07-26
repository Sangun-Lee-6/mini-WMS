package demo.mini_WMS.repository;


import demo.mini_WMS.domain.OrderLine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderLineRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(OrderLine line) {
        if (line.getId() == null) {
            em.persist(line);
        } else {
            em.merge(line);
        }
    }

    public List<OrderLine> findByOrderId(Long orderId) {
        return em.createQuery(
                        "SELECT ol FROM OrderLine ol WHERE ol.order.id = :orderId", OrderLine.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
