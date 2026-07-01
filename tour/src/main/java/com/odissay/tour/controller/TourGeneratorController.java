package com.odissay.tour.controller;

import com.odissay.tour.model.dto.reponse.TourDetailResponse;
import com.odissay.tour.model.dto.request.TourGeneratorRequest;
import com.odissay.tour.service.TourGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Validated
public class TourGeneratorController {

    private final TourGeneratorService tourGeneratorService;

    @PostMapping("/{branchId}")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse>generateTour(
            @PathVariable int branchId, @RequestBody @Valid TourGeneratorRequest req
            ){
       System.out.println("11111111111111111");

       return new ResponseEntity<>(tourGeneratorService.generateTour(branchId,req), HttpStatus.CREATED) ;


    }
}
