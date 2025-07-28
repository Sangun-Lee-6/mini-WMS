package demo.mini_WMS.repository.inbound;

import demo.mini_WMS.domain.Inbound;

import java.util.List;
import java.util.Optional;

public interface InboundRepository {

    Inbound save(Inbound inbound);

    Optional<Inbound> findById(Long id);

    List<Inbound> findByWarehouseIdOrderByInboundDateDesc(Long warehouseId);
}
