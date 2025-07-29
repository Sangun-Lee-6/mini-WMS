package demo.mini_WMS.repository;

import demo.mini_WMS.domain.OrderStatus;
import demo.mini_WMS.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Orders orders) {
        em.persist(orders);
    }

    public Orders findById(Long id) {
        return em.find(Orders.class, id);
    }

    public List<Orders> findByStatus(OrderStatus status) {
        return em.createQuery("SELECT o FROM Orders o WHERE o.status = :status", Orders.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Orders> findWaitingOrders() {
        return em.createQuery("SELECT o FROM Orders o WHERE o.status = :status ORDER BY o.createdAt DESC", Orders.class)
                .setParameter("status", OrderStatus.WAITING)
                .getResultList();
    }

    @Transactional
    public int updateStatusByStatus(OrderStatus oldStatus, OrderStatus newStatus) {
        return em.createQuery("UPDATE Orders o SET o.status = :newStatus WHERE o.status = :oldStatus")
                .setParameter("newStatus", newStatus)
                .setParameter("oldStatus", oldStatus)
                .executeUpdate();
    }

}