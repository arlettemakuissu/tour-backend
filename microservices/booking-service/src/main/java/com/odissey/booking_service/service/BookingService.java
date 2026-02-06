package com.odissey.booking_service.service;

import com.odissey.booking_service.dto.request.BookingsTourRequest;
import com.odissey.booking_service.dto.response.PdfBookingResponse;
import com.odissey.booking_service.dto.response.TourInfoForBookingResponse;
import com.odissey.booking_service.entity.Booking;
import com.odissey.booking_service.exception.BookingException;
import com.odissey.booking_service.exception.ErrMsg;
import com.odissey.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
    private final PdfService pdfService;

    private final static String SERVICE_ACCOUNT = "SERVICE_ACCOUNT";
    private final static String URI = "http://localhost:9090/tours/is_bookable_tour";
    private final static String URI_UNBOOK = "http://localhost:9090/tours/unbook_tour/";

    public PdfBookingResponse create(int customerId, int tourId) {
        long bookings = bookingRepository.countByCustomerIdAndTourIdAndCanceledFalse(customerId, tourId);
        if(bookings > 0)
            throw new BookingException(ErrMsg.TOUR_ALREADY_BOOKED);
        BookingsTourRequest bookingsTourRequest = new BookingsTourRequest(tourId, bookingRepository.countByTourIdAndCanceledFalse(tourId));
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Service", SERVICE_ACCOUNT);
            HttpEntity<BookingsTourRequest> entity = new HttpEntity<>(bookingsTourRequest, headers);
            ResponseEntity<TourInfoForBookingResponse> response =
                    restTemplate.exchange(
                            URI,
                            HttpMethod.POST,
                            entity,
                            TourInfoForBookingResponse.class
                    );
            if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() !=null && response.getBody().isBookable()) {

                String code = UUID.randomUUID().toString();
                Booking b = new Booking(customerId, tourId, code);

                byte[] pdf = pdfService.createPdf(response.getBody(), code);
                PdfBookingResponse pdfBookingResponse = new PdfBookingResponse(code, pdf);

                bookingRepository.save(b);
                return pdfBookingResponse;
            }
            else if (response.getStatusCode().equals(HttpStatus.NO_CONTENT))
                throw new BookingException(ErrMsg.TOUR_NOT_BOOKABLE);
            else {
                log.info(">>> " + ErrMsg.TOUR_NOT_BOOKABLE + " - {}", response.getStatusCode().toString());
                throw new BookingException(ErrMsg.TOUR_NOT_BOOKABLE_FOR_UNKNOWN_REASON);
            }
        } catch (RestClientException ex){
            log.error(">>> {}", ex.getMessage());
            throw new BookingException(ErrMsg.TOUR_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            throw new BookingException(ErrMsg.PDF_SERVICE_UNAVAILABLE);
        }
    }


    @Transactional
    public String unBooking(int customerId, int tourId, String code) {
        // verificare esistenza prenotazione
        Booking booking = bookingRepository.findByCode(code)
                .orElseThrow(()-> new BookingException(ErrMsg.BOOKING_NOT_FOUND));
        // flag 'canceled' a true su Booking
        booking.setCanceled(true);
        booking.setUpdatedBy(customerId);
        // modificare status del tour da SOLD_OUT a OPEN
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Service", SERVICE_ACCOUNT);
            HttpEntity<Integer> entity = new HttpEntity<>(tourId, headers);
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            URI_UNBOOK+tourId,
                            HttpMethod.PATCH,
                            entity,
                            Void.class
                    );
            if(response.getStatusCode().is2xxSuccessful())
                return "La prenotazione al tour è stata cancellata con successo";
        } catch (RestClientException ex){
            log.error(">>> {}", ex.getMessage());
        }
        return ErrMsg.TOUR_SERVICE_UNAVAILABLE;

    }
}
