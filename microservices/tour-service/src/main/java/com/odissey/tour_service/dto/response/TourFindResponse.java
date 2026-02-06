package com.odissey.tour_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class TourFindResponse {

    private int id;
    private String name;
    private String countryName;
    private LocalDate startDate;
    private LocalDate endDate;
    private float price;
    private String currency;
}
