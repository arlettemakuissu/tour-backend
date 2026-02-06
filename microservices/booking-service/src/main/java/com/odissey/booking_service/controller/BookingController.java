package com.odissey.booking_service.controller;

import com.odissey.booking_service.dto.response.PdfBookingResponse;
import com.odissey.booking_service.service.BookingService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    // prenotazione di un tour
    @PostMapping(value = "/{tourId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> booking(
            @PathVariable @Min(1) int tourId,
            @RequestHeader("X-User-Id") @Min(1) int customerId){
        PdfBookingResponse pdfBookingResponse = bookingService.create(customerId, tourId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\" "+pdfBookingResponse.getFilename()+".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBookingResponse.getPdf());
    }

    // rinuncia alla prenotazione
    @PatchMapping("/{tourId}/{code}")
    public ResponseEntity<String> unBooking(
            @PathVariable @Min(1) int tourId,
            @PathVariable @NotBlank  String code,
            @RequestHeader("X-User-Id") @Min(1) int customerId){
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.unBooking(customerId, tourId, code));
    }
}