package demo.mini_WMS.repository;


import demo.mini_WMS.domain.InboundReceipt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InboundReceiptRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(InboundReceipt receipt) {
        em.persist(receipt);
    }

    public Optional<InboundReceipt> findById(Long id) {
        return Optional.ofNullable(em.find(InboundReceipt.class, id));
    }
}
