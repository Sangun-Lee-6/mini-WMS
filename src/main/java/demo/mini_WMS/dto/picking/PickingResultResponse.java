package demo.mini_WMS.dto.picking;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PickingResultResponse {
    private String algorithm;
    private int totalItems;
    private int totalDistance;
    private int totalTime;
    private List<PickingItemDto> items;

    @Data
    @AllArgsConstructor
    public static class PickingItemDto {
        private Long productId;
        private String productName;
        private Long locationId;
        private String locationCode;
        private int quantity;
    }
}
