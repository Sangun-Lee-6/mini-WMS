package demo.mini_WMS.controller;

import demo.mini_WMS.dto.inbound.InboundCreateRequest;
import demo.mini_WMS.dto.inbound.InboundListResponse;
import demo.mini_WMS.service.InboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundService inboundService;

    /**
     * 입고 등록 API
     */
    @PostMapping
    public ResponseEntity<Void> createInbound(@RequestBody @Valid InboundCreateRequest request) {
        inboundService.createInbound(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 입고 현황 조회 API
     */
    @GetMapping
    public List<InboundListResponse> getInboundList() {
        return inboundService.getInboundList(1L).stream()
                .map(InboundListResponse::from)
                .toList();
    }
}

