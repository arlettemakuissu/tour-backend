package com.odissay.tour.controller;

import com.odissay.tour.model.dto.reponse.AgencyResponse;
import com.odissay.tour.model.dto.reponse.CustomErrorResponse;
import com.odissay.tour.model.dto.request.AgencyRequest;
import com.odissay.tour.model.entity.Agency;
import com.odissay.tour.service.AgencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agencies") //mi cercherà tutto nella tabella countries
@Validated
public class AgencyController {

    private final AgencyService agencyService;

    @Operation(
            summary = "CREATE AGENCY",
            description = "Questo metodo serve a inserire una nuova agenzia sul database",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode="201", description="Agenzia inserita con successo sul database.", content = @Content(schema = @Schema(implementation = AgencyResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, city, address, vat o countryId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Country di riferimento non trovata o disattivata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Il vat è già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<AgencyResponse> create(@RequestBody @Valid AgencyRequest req){

         return new ResponseEntity<>(agencyService.save(req), HttpStatus.CREATED);
    }

    @Operation(
            summary = "GET AGENCIES",
            description = "Questo metodo restituisce la lista delle agenzie.",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco delle agenzie restituito correttamente.", content = @Content(array = @ArraySchema(schema = @Schema(implementation =  AgencyResponse.class)))),
                    @ApiResponse(responseCode="404", description="Nessuna agenzia attiva presente sul database.a", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<AgencyResponse>> getAgencies(){
        return new ResponseEntity<>(agencyService.findAllAgencies(), HttpStatus.OK);
    }
    @Operation(
            summary = "GET AGENCY",
            description = "Questo metodo restituisce un'agenzia' in base all'id.",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode="200", description="Agenzia trovata.", content = @Content(schema = @Schema(implementation = AgencyResponse.class))),
                    @ApiResponse(responseCode="400", description="L'id dell'agenzia' non è un valore valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Agenzia non trovata o disattivata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/{id}") public ResponseEntity<AgencyResponse> getAgency
            ( @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int id ){
        return new ResponseEntity<>(agencyService.getAgency(id), HttpStatus.OK); }
    @Operation(
            summary = "GET AGENCIES BY COUNTRY",
            description = "Questo metodo restituisce la lista di filiali appartenenti ad una precisa nazione.",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco agenzie restituito correttamente.", content = @Content(array = @ArraySchema(schema = @Schema(implementation =  AgencyResponse.class)))),
                    @ApiResponse(responseCode="400", description="Il valore di countryId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nazione di riferimento non trovata o disattivata oppure non esistono agenzie associate alla nazione.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/countries/{countryId}") // localhost:8081/agencies/countries/{countryId}
    public ResponseEntity<List<AgencyResponse>> getAgenciesByCountry(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") short countryId
    ) {
        return new ResponseEntity<>(agencyService.getAgenciesByCountry(countryId), HttpStatus.OK);
    }
    @Operation(
            summary = "UPDATE AGENCY",
            description = "Questo metodo serve per aggiornare sul database l'agenzia' identificata dal proprio id.",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode="200", description="Agenzia aggiornata con successo sul database.", content = @Content(schema = @Schema(implementation = AgencyResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, city, address, vat o countryId o id non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Agenzia o nazione non trovate o disattivate.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Il vat è già presenti sul database su un'agenzia diversa da quella che si sta aggiornando", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="422", description="L'agenzia di riferimento non è attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<AgencyResponse> update(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int id,
            @RequestBody @Valid AgencyRequest req){
        return new ResponseEntity<>(agencyService.update(id, req), HttpStatus.OK);
    }
    @Operation(
            summary = "ACTIVATE/DEACTIVATE AGENCY",
            description = "Questo metodo serve per modificare lo stato di una filiale da attivo a disattivo e viceversa.",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode="204", description="Stato dell'agenzia aggiornato con successo.", content = @Content(schema = @Schema(implementation = AgencyResponse.class))),
                    @ApiResponse(responseCode="400", description="L'id dell'agenzia non presenta un valore valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> switchAgencyStatus(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int id
    ){
        agencyService.switchAgencyStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
