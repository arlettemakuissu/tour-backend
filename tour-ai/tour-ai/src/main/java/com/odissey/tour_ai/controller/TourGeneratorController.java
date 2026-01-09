package com.odissey.tour_ai.controller;


import com.odissey.tour_ai.dto.TourRequest;
import com.odissey.tour_ai.dto.TourResponse;
import com.odissey.tour_ai.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
public class TourGeneratorController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<TourResponse>generateTour(@RequestBody @Valid TourRequest req){

        return new  ResponseEntity<>(tourService.generateTour(req),HttpStatus.CREATED);
    }
}
