package com.odissay.tour.controller;


import com.odissay.tour.model.dto.reponse.RatingResponse;
import com.odissay.tour.service.RatingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Rating")
@Validated
public class RatingController {

    private final RatingService ratingService;




    @PostMapping("/{tourId}/{rate}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<RatingResponse>create(
            @PathVariable @Min(value = 1, message = "L'id del customer deve essere un numero intero positivo") int tourId,
            @PathVariable
            @Min(value = 1, message = "Il voto non può essere inferiore a 1")
            @Max(value = 5, message = "Il voto non può essere maggiore di 5") int rate,
            @AuthenticationPrincipal UserDetails userDetails
    ){


        return new ResponseEntity<>(ratingService.create( tourId, rate,userDetails), HttpStatus.CREATED);
    }

}
