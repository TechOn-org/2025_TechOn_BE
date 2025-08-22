package com.techon.server.domain.estimation.dto;

import com.techon.server.domain.estimation.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstimationRequestDTO (
        @NotBlank String text,
        @NotBlank String modelName,
        @NotNull Category category
        ){}
