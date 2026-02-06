package com.odissey.agency_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgencyRequest(

        @NotBlank
        String name,
        @NotBlank
        String vat,
        @NotBlank
        String address,
        @NotBlank
        String city,
        @NotBlank @Size(min = 2, max = 2)
        String countryCode
) {
}
