package com.odissey.agency_service.controller;

import com.odissey.agency_service.dto.request.AgencyRequest;
import com.odissey.agency_service.dto.response.AgencyActiveListResponse;
import com.odissey.agency_service.dto.response.AgencyListResponse;
import com.odissey.agency_service.dto.response.AgencyResponse;
import com.odissey.agency_service.service.AgencyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agencies")
@RequiredArgsConstructor
@Validated
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping
    public ResponseEntity<@NonNull AgencyResponse> create(
            @RequestBody @Valid AgencyRequest agencyRequest,
            @RequestHeader("X-User-Id") @Min(1) int createdBy
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(agencyService.create(agencyRequest, createdBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull AgencyResponse> update(
            @RequestBody @Valid AgencyRequest agencyRequest,
            @PathVariable @Min(1) int id,
            @RequestHeader("X-User-Id") @Min(1) int updatedBy
            ){
        return ResponseEntity.status(HttpStatus.OK).body(agencyService.update(id, agencyRequest, updatedBy));
    }

    @GetMapping("/all")
    public ResponseEntity<@NonNull List<AgencyListResponse>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(agencyService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<@NonNull List<AgencyActiveListResponse>> getActive(){
        return ResponseEntity.status(HttpStatus.OK).body(agencyService.getActive());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<@NonNull AgencyListResponse> switchStatus(
            @PathVariable @Min(1) int id,
            @RequestHeader("X-User-Id") @Min(1) int updatedBy
    ){
        return ResponseEntity.status(HttpStatus.OK).body(agencyService.switchStatus(id, updatedBy));
    }

}
