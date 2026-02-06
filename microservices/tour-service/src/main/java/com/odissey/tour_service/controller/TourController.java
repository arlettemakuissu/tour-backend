package com.odissey.tour_service.controller;

import com.odissey.tour_service.dto.request.BookingsTourRequest;
import com.odissey.tour_service.dto.request.ReorderImagesRequest;
import com.odissey.tour_service.dto.request.TourRequest;
import com.odissey.tour_service.dto.response.TourDetailResponse;
import com.odissey.tour_service.dto.response.TourFindResponse;
import com.odissey.tour_service.dto.response.TourHomePageResponsePaginated;
import com.odissey.tour_service.dto.response.TourInfoForBookingResponse;
import com.odissey.tour_service.service.HeroImageService;
import com.odissey.tour_service.service.TourService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@Validated
public class TourController {

    private final TourService tourService;
    private final HeroImageService heroImageService;

    @PostMapping
    public ResponseEntity<TourDetailResponse> create(
            @RequestBody @Valid TourRequest tourRequest,
            @RequestHeader("X-User-Id") @Positive int createdBy){
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.create(tourRequest, createdBy));
    }

    // Modifica tour fintantoché è in stato WORK_IN_PROGRESS
    @PutMapping("/{id}")
    public ResponseEntity<TourDetailResponse> update(
            @PathVariable @Positive int id,
            @RequestBody @Valid TourRequest tourRequest,
            @RequestHeader("X-User-Id") @Positive int updatedBy){
        return ResponseEntity.status(HttpStatus.OK).body(tourService.update(id, tourRequest, updatedBy));
    }

    // Pubblicazione tour -> cambio stato da WORK_IN_PROGRESS a OPEN
    @PatchMapping("/{id}")
    public ResponseEntity<TourDetailResponse> changeStatus(
            @PathVariable @Positive int id,
            @RequestHeader("X-User-Id") @Positive int updatedBy
    ){
        return ResponseEntity.status(HttpStatus.OK).body(tourService.publishTour(id, updatedBy));
    }

    // Caricamento immagini del tour (su database)
    @PatchMapping(value = "/{id}/hero_images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createHeroImages(
            @PathVariable @Positive int id,
            @RequestHeader("X-User-Id") @Positive int createdBy,
            @RequestPart @NotEmpty MultipartFile[] files
            ) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(heroImageService.createHeroImages(id, createdBy, files));
    }

    // GET del dettaglio del tour
    @GetMapping("/public/{id}")
    public ResponseEntity<TourDetailResponse> getTour(@PathVariable @Positive int id){
        return ResponseEntity.status(HttpStatus.OK).body(tourService.getTour(id));
    }

    // Eliminazione di un'immagine collegata ad un tour
    @DeleteMapping("/hero_images/{id}")
    public ResponseEntity<Void> deleteHeroImage(@PathVariable @NotBlank String id){
        heroImageService.deleteHeroImage(id);
        return ResponseEntity.noContent().build();
    }

    // Riordino dei progressivi delle immagini
    @PatchMapping("/hero_images/reorder")
    public ResponseEntity<Void> reorderImages(
            @RequestBody @NotEmpty @Valid List<ReorderImagesRequest> newOrders,
            @RequestHeader("X-User-Id") @Positive int updatedBy){
        heroImageService.reorderImages(newOrders, updatedBy);
        return ResponseEntity.noContent().build();
    }

    // Tour in home page
    // criteri di selezione: TourStatus.OPEN AND inHomePage = true AND
    // il giorno attuale più vicino al giorno di partenza
    @GetMapping("/public/home_page")
    public ResponseEntity<TourHomePageResponsePaginated> toursInHomePage(
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "0")  int pageNumber
    ){
        return ResponseEntity.status(HttpStatus.OK).body(tourService.toursInHomePage(pageSize, pageNumber));
    }

    // Ricerca dei tour per parola chiave in titolo, descrizione e nome nazione
    @GetMapping("/public/find")
    public ResponseEntity<List<TourFindResponse>> findTours(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end
            ){
        return ResponseEntity.status(HttpStatus.OK).body(tourService.findTours(keyword, start, end));
    }


    // ----------- CHIAMATE REST TEMPLATE DA BOOKING-SERVICE

    @PostMapping("/is_bookable_tour")
    public ResponseEntity<TourInfoForBookingResponse> isBookableTour(@RequestBody BookingsTourRequest bookingsTourRequest){
        TourInfoForBookingResponse tourInfoForBookingResponse = tourService.isBookableTour(bookingsTourRequest);
        if(tourInfoForBookingResponse == null)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.status(HttpStatus.OK).body(tourInfoForBookingResponse);
    }

    @PatchMapping("/unbook_tour/{tourId}")
    public ResponseEntity<Void> unbookTour(@PathVariable @Positive int tourId){
        tourService.unbookTour(tourId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
