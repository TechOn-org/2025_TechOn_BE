package com.techon.server.domain.estimation.controller;

import com.techon.server.domain.estimation.dto.EstimationRequestDTO;
import com.techon.server.domain.estimation.dto.EstimationResponseDTO;
import com.techon.server.domain.estimation.entity.Estimation;
import com.techon.server.domain.estimation.service.EstimationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estimation")
public class EstimationController {

    private final EstimationService estimationService;

    @Operation(
            summary = "견적서 진단 API",
            description = "증상과 모델명, 카테고리(MOB(휴대폰/테블릿)/COM(노트북/PC)/APP(생활가전)/DEV(주변기기/기타))를 입력하면 진단 결과, 필요 부품, 견적(최소, 최대, 합본), 카테고리를 반환합니다."
    )
    @PostMapping()
    public ResponseEntity<EstimationResponseDTO> estimate(
            @RequestBody @Valid EstimationRequestDTO request) {
        return ResponseEntity.ok(estimationService.analyze(request));
    }
}
