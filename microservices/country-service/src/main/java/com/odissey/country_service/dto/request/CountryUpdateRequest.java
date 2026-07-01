package com.odissey.country_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryUpdateRequest(
        @NotBlank @Size(min = 1, max = 255)
        String name,
        @NotBlank @Size(min = 1, max = 255)
        String currency
) {
}
