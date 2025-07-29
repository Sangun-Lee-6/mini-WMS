package demo.mini_WMS.controller;

import demo.mini_WMS.dto.picking.PickingRequest;
import demo.mini_WMS.dto.picking.PickingResultResponse;
import demo.mini_WMS.service.PickingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/picking")
public class PickingController {

    private final PickingService pickingService;

    /**
     * 피킹 실행 및 결과 반환
     */
    @PostMapping
    public PickingResultResponse runPicking(@RequestBody PickingRequest request) {
        return pickingService.executePicking(request.getAlgorithm());
    }
}