package com.odissey.country_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CountryRequest(
        @NotBlank @Size(min = 2, max = 2)
        String id,
        @NotBlank @Size(min = 1, max = 255)
        String name,
        @NotBlank @Size(min = 1, max = 255)
        String currency,
        @NotNull @Min(1)
        int createdBy
) {
}
