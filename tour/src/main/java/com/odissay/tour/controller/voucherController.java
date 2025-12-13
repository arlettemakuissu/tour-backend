package com.odissay.tour.controller;


import com.odissay.tour.model.dto.reponse.VoucherReceiptResponse;
import com.odissay.tour.model.dto.request.VoucherRequest;
import com.odissay.tour.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/vouchers")
public class voucherController {


    private final VoucherService voucherService;





    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<VoucherReceiptResponse> create(@RequestBody @Valid VoucherRequest req){

        return  new ResponseEntity<>(voucherService.create(req), HttpStatus.CREATED);
    }


}
