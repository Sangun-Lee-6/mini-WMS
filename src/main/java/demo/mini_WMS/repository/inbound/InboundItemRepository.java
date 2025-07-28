package demo.mini_WMS.repository.inbound;

import demo.mini_WMS.domain.InboundItem;

import java.util.List;

public interface InboundItemRepository {
    InboundItem save(InboundItem item);
    List<InboundItem> findByInboundId(Long inboundId);
}
