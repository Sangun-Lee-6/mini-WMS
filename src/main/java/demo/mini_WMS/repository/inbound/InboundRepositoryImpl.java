package demo.mini_WMS.repository.inbound;

import demo.mini_WMS.domain.Inbound;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class InboundRepositoryImpl implements InboundRepository{

    private final EntityManager em;

    @Override
    public Inbound save(Inbound inbound) {
        em.persist(inbound);
        return inbound;
    }

    @Override
    public Optional<Inbound> findById(Long id) {
        return Optional.ofNullable((em.find(Inbound.class, id)));
    }

    @Override
    public List<Inbound> findByWarehouseIdOrderByInboundDateDesc(Long warehouseId) {
        return em.createQuery(
                        "SELECT i FROM Inbound i WHERE i.warehouse.id = :wid ORDER BY i.inboundDate DESC", Inbound.class)
                .setParameter("wid", warehouseId)
                .getResultList();
    }
}
