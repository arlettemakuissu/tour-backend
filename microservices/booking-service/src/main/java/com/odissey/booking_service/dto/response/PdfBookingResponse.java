package com.odissey.booking_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PdfBookingResponse {
    private String filename;
    private byte[] pdf;
}
