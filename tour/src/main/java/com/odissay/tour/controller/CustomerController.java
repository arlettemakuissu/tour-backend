package com.odissay.tour.controller;


import com.odissay.tour.model.dto.reponse.CustomErrorResponse;
import com.odissay.tour.model.dto.reponse.CustomerDetailResponse;
import com.odissay.tour.model.dto.reponse.CustomerListResponse;
import com.odissay.tour.model.dto.reponse.CustomerResponse;

import com.odissay.tour.model.dto.request.CustomerUpdateRequest;

import com.odissay.tour.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer") //mi cercherà tutto nella tabella countries
@Validated

public class CustomerController {

    private  CustomerResponse customerResponse;
    private final CustomerService customerService;


    @Operation(
            summary = "UPDATE CUSTOMER",
            description = "Questo metodo consente ad un utente (autenticato) di aggiornare le proprie informazioni realtive a username, email, firstname e lastname, address, city e country.",
            tags = {"Customer"},
            responses = {
                    @ApiResponse(responseCode="200", description="Aggiornamento avvenuto con successo. E' necessario riloggarsi", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di username, email, firstname, lastname, address, city o country non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Username o email già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )

    @PutMapping
    public ResponseEntity<CustomerResponse> updateCustomer(

             @AuthenticationPrincipal UserDetails userDetails,
             @RequestBody @Valid CustomerUpdateRequest  req
             )

     {
         System.out.println("customer");
         CustomerResponse customer = customerService.updateCustomer(userDetails,req);
             System.out.println(customer);
         System.out.println("customer");
              return new ResponseEntity(customer,HttpStatus.OK);

     }

    @Operation(
            summary = "GET ACTIVE CUSTOMERS",
            description = "Questa metodo restituisce una lista di customer",
            tags = {"Customer"},
            responses = {
                    @ApiResponse(responseCode="200", description="Aggiornamento avvenuto con successo. E' necessario riloggarsi", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode="404", description="Nessun customer activa presente su database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),

    })
     @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<CustomerListResponse>> getActiveCustomersByLastLoginAndCountry(){


        List<CustomerListResponse> list = customerService.getActiveCustomersByLastLoginAndCountry();

        if(list.isEmpty())
            return new ResponseEntity<>(list,HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(list, HttpStatus.OK);

    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<CustomerDetailResponse> getActiveCustomersByLastLoginAndCountryAndId(
            @PathVariable @NotNull(message = "l'indentificativo è obbligattorio") @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") int id

    ){
        return new ResponseEntity<>(customerService.getActiveCustomerByLastLoginAndCountryAndId(id), HttpStatus.OK);

    }

}
