package com.odissey.country_service.controller;

import com.odissey.country_service.dto.request.CountryRequest;
import com.odissey.country_service.dto.request.CountryUpdateRequest;
import com.odissey.country_service.dto.response.CountryDetailResponse;
import com.odissey.country_service.dto.response.CountryResponse;
import com.odissey.country_service.service.CountryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Validated
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    public ResponseEntity<CountryResponse> create(@RequestBody @Valid CountryRequest countryRequest){
        //  return new ResponseEntity<>(countryService.create(countryRequest), HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(countryRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountryResponse> update(
            @PathVariable @Size(min = 2, max = 2) String id,
            @RequestBody @Valid CountryUpdateRequest countryUpdateRequest){
        return ResponseEntity.status(HttpStatus.OK).body(countryService.update(id.toUpperCase(),countryUpdateRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CountryDetailResponse> switchStatus(@PathVariable @Size(min = 2, max = 2) String id){
        return ResponseEntity.status(HttpStatus.OK).body(countryService.switchStatus(id.toUpperCase()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CountryDetailResponse>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<CountryResponse>> getActiveCountries(){
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getActiveCountries());
    }


}
