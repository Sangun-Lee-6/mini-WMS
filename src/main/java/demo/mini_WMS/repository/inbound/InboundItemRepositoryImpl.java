package demo.mini_WMS.repository.inbound;

import demo.mini_WMS.domain.InboundItem;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InboundItemRepositoryImpl implements InboundItemRepository {

    private final EntityManager em;

    @Override
    public InboundItem save(InboundItem item) {
        em.persist(item);
        return item;
    }

    @Override
    public List<InboundItem> findByInboundId(Long inboundId) {
        return em.createQuery(
                        "SELECT ii FROM InboundItem ii WHERE ii.inbound.id = :inboundId", InboundItem.class)
                .setParameter("inboundId", inboundId)
                .getResultList();
    }
}
