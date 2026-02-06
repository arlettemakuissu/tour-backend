package com.odissey.tour_service.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record TourRequest(
        @NotBlank @Size(min = 1, max = 255)
        String name,
        @NotBlank @Size(min = 10, max = 15360)
        String description,
        @Future @NotNull
        LocalDate startDate,
        @Future @NotNull
        LocalDate endDate,
        @Positive @NotNull
        short minPax,
        @Positive @NotNull
        short maxPax,
        @Positive @NotNull @Digits(integer = 4, fraction = 2) // max price = 9999.99
        float price,
        @NotBlank @Size(min = 2, max = 2)
        String countryCode,
        @Positive @NotNull
        int agencyId
) {
}
