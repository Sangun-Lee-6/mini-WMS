package demo.mini_WMS.dto.picking;

import demo.mini_WMS.domain.picking.PickingItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PickingInput {
    private List<PickingItem> items;
}