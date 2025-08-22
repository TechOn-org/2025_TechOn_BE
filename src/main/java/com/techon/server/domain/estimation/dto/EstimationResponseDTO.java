package com.techon.server.domain.estimation.dto;

import com.techon.server.domain.estimation.entity.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record EstimationResponseDTO(
        Long estimationId,
        String diagnosis,
        List<String> requiredParts,
        Integer estimateMin,
        Integer estimateMax,
        String estimateRaw,
        Category category,
        List<String> matchedKeywords,
        Integer score,

        String normalizedText
) {}
