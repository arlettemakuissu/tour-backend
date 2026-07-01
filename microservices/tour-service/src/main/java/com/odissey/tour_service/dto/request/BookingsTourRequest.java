package com.odissey.tour_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class BookingsTourRequest {

    private int tourId;
    private long bookings;
}
