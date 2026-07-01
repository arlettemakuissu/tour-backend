package com.odissey.tour_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReorderImagesRequest(
        @NotBlank
        String id,
        @NotNull
        int prg
) {
}
