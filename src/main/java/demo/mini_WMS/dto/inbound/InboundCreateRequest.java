package demo.mini_WMS.dto.inbound;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class InboundCreateRequest {
    @NotNull
    private Long warehouseId;

    @NotBlank
    private String supplier;

    @NotEmpty
    private List<Item> items;

    @Getter
    public static class Item{
        @NotNull
        private Long productId;

        @NotNull
        private Long quantity;
    }
}
