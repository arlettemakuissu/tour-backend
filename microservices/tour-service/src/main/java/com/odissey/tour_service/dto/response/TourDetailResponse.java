package com.odissey.tour_service.dto.response;

import com.odissey.tour_service.entity.Tour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TourDetailResponse {

    private int id;
    private String name;
    private String description;
    private List<HeroImageResponse> heroImages = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private short minPax;
    private short maxPax;
    private float price;
    private String status;
    private String countryName;
    private String currency;
    private int agencyId;
    private String agencyName;

    public TourDetailResponse(int id, String name, String description, LocalDate startDate, LocalDate endDate, short minPax, short maxPax, float price, String status, String countryName, String currency, int agencyId, String agencyName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minPax = minPax;
        this.maxPax = maxPax;
        this.price = price;
        this.status = status;
        this.countryName = countryName;
        this.currency = currency;
        this.agencyId = agencyId;
        this.agencyName = agencyName;
    }

    public static TourDetailResponse fromEntityToDto(Tour tour){
        return new TourDetailResponse(
                tour.getId(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getMinPax(),
                tour.getMaxPax(),
                tour.getPrice(),
                tour.getStatus().name(),
                tour.getCountry().getName(),
                tour.getCountry().getCurrency(),
                tour.getAgency().getId(),
                tour.getAgency().getName()
        );
    }
}
