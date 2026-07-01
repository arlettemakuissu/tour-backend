package com.odissey.tour_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class TourHomePageResponse {

    private int id;
    private String title;
    private float price;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long duration;
    private String countryName;
    private HeroImageResponse image;
    private String currency;

    public TourHomePageResponse(int id, String title, float price, LocalDate startDate, LocalDate endDate, String countryName, String currency) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.countryName = countryName;
        this.currency = currency;
    }
}
