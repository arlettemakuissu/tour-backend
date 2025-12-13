package com.odissay.tour.controller;

import com.odissay.tour.service.BookingService;
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
@RequestMapping("/booking") //mi cercherà tutto nella tabella countries
@Validated
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/{tourId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public  ResponseEntity<String>bookingTour(

            @PathVariable @Min(value =1,message = "L'id del tour deve essere un numero intero maggiore di zero" ) int tourId,
            @AuthenticationPrincipal UserDetails userDetails
            ){
        try{

        System.out.println("helllooooooooooooooo");
        System.out.println(tourId);
        System.out.println("éééééééééééééé");
        System.out.println(userDetails.getUsername());

         String  booking = bookingService.bookingTour(tourId,userDetails);
        System.out.println("yessssssssssssssooooooo");
        return new ResponseEntity<>(booking, HttpStatus.CREATED);

        } catch (Exception e){
            System.out.println("messaggio di errore " + e.getMessage());
            throw  e ;
        }
    }
}
