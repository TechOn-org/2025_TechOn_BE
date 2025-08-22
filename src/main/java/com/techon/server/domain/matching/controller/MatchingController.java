package com.techon.server.domain.matching.controller;

import com.techon.server.domain.matching.dto.MatchingRequestDTO;
import com.techon.server.domain.matching.dto.MatchingResponseDTO;
import com.techon.server.domain.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matching")
@Validated
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(
            summary = "주변 가게 매칭 API",
            description = "시, 구, 동, 카테고리(MOB(휴대폰/테블릿)/COM(노트북/PC)/APP(생활가전)/DEV(주변기기/기타)), 검색반경(미터단위, 최대2000 미터)를 입력하면 상가업소번호, 상호명, 업종분류, 지번주소, 도로명주소, 건물층정보를 반환합니다."
    )
    @GetMapping("/nearby")
    public Mono<ResponseEntity<List<MatchingResponseDTO>>> nearbyByCategory(@Valid MatchingRequestDTO request) {
        return matchingService.find(request.si(), request.gu(), request.dong(), request.cat(), request.radiusMeters()).map(ResponseEntity::ok);
    }

}
