package com.techon.server.domain.matching.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MatchingRequestDTO (
        @NotBlank String si,
        @NotBlank String gu,
        @NotBlank String dong,
        @NotBlank String cat,
        @Positive int radiusMeters
) {}
