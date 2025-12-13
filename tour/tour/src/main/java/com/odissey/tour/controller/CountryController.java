package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.CountryRequest;
import com.odissey.tour.model.dto.response.CountryResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.service.CountryService;
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
@RequestMapping("/countries")
@Validated
public class CountryController {

    private final CountryService countryService;


    @Operation(
            summary = "CREATE COUNTRY",
            description = "Questo metodo serve a inserire sul database una nuova nazione.",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="201", description="Nazione inserita con successo sul database.", content = @Content(schema = @Schema(implementation = CountryResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, code o currency non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Codice o nome nazione già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping // http://localhost:8081/api/countries
    public ResponseEntity<CountryResponse> save(@RequestBody @Valid CountryRequest req){
        return new ResponseEntity<>(countryService.save(req), HttpStatus.CREATED);
    }


    @Operation(
            summary = "GET ACTIVE COUNTRIES",
            description = "Questo metodo restituisce l'elenco di nazioni <i>attive</i>",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco restituito con successo.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))),
                    @ApiResponse(responseCode="404", description="Nessuna nazione <i>attiva</i> trovata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/active") // http://localhost:8081/api/countries/active
    public ResponseEntity<List<CountryResponse>> getActiveCountries(){
        List<CountryResponse> list = countryService.getActiveCountries();
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @Operation(
            summary = "GET ALL COUNTRIES",
            description = "Questo metodo restituisce l'elenco di tutte le nazioni",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco restituito con successo.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))),
                    @ApiResponse(responseCode="404", description="Nessuna nazione trovata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping // http://localhost:8081/api/countries
    public ResponseEntity<List<CountryResponse>> getCountries(){
        List<CountryResponse> list = countryService.getCountries();
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @Operation(
            summary = "UPDATE COUNTRY",
            description = "Questo metodo serve per aggiornare sul database la nazione identificata dal proprio id.",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="200", description="Nazione aggiornata con successo sul database.", content = @Content(schema = @Schema(implementation = CountryResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di id, name, code o currency non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Codice o nome nazione già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PutMapping("/{id}") // http://localhost:8081/api/countries/{id}
    public ResponseEntity<CountryResponse> update(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero") short id,
            @RequestBody @Valid CountryRequest req){
        return new ResponseEntity<CountryResponse>(countryService.update(id, req), HttpStatus.OK);
    }


    @Operation(
            summary = "ACTIVATE/DEACTIVATE COUNTRY",
            description = "Questo metodo serve per modificare lo stato di una nazione da attivo a disattivo e viceversa.",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="204", description="Stato della nazione aggiornato con successo.", content = @Content(schema = @Schema(implementation = CountryResponse.class))),
                    @ApiResponse(responseCode="400", description="L'id della nazione non presenta un valore valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}") // http://localhost:8081/api/countries/{id}
    public ResponseEntity<Void> toggleCountryStatus(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero") short id
    ){
        countryService.switchCountryStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(
            summary = "GET COUNTRY",
            description = "Questo metodo restituisce la nazione identificata da un determinato id.",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="200", description="Nazione trovata.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))),
                    @ApiResponse(responseCode="400", description="L'id della nazione non presenta un valore valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nazione non trovata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/{id}") // http://localhost:8081/api/countries/{id}
    public ResponseEntity<CountryResponse> getCountry(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero") short id
    ){
        return new ResponseEntity<>(countryService.getCountry(id), HttpStatus.OK);
    }
}
