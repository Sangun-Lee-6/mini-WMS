package demo.mini_WMS.dto.inbound;

import demo.mini_WMS.domain.Inbound;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InboundListResponse {

    private Long id;
    private String supplier;
    private LocalDateTime inboundDate;
    private int itemCount;
    private String warehouseName;
    private String warehouseLocation;

    public static InboundListResponse from(Inbound inbound) {
        return InboundListResponse.builder()
                .id(inbound.getId())
                .supplier(inbound.getSupplier())
                .inboundDate(inbound.getInboundDate())
                .itemCount(inbound.getItems() != null ? inbound.getItems().size() : 0)
                .warehouseName(inbound.getWarehouse().getName())
                .warehouseLocation(inbound.getWarehouse().getLocation())
                .build();
    }
}