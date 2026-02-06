package com.odissey.booking_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class TourInfoForBookingResponse {

    private String name;
    private LocalDate startDate;
    private String country;
    private String agency;
    private float price;
    private boolean bookable;
}
