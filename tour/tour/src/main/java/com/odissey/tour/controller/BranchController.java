package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.BranchRequest;
import com.odissey.tour.model.dto.response.BranchResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.service.BranchService;
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
@RequestMapping("/branches")
@Validated
public class BranchController {

    private final BranchService branchService;


    @Operation(
            summary = "CREATE BRANCH",
            description = "Questo metodo serve a inserire una nuova filiale sul database",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode="201", description="Filiale inserita con successo sul database.", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, city, address, vat o agencyId o countryId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Agenzia di riferimento non trovata o disattivata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="La coppia di valori 'name/agencyId' oppure il vat sono già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="422", description="L'agenzia di riferimento non è attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<BranchResponse> save(@RequestBody @Valid BranchRequest req){
        return new ResponseEntity<>(branchService.save(req), HttpStatus.CREATED);
    }


    @Operation(
            summary = "GET BRANCHES BY AGENCY",
            description = "Questo metodo restituisce la lista di filiali appartenenti ad una precisa agenzia.",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco filiali restituito correttamente.", content = @Content(array = @ArraySchema(schema = @Schema(implementation =  BranchResponse.class)))),
                    @ApiResponse(responseCode="400", description="Il valore di agencyId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Agenzia di riferimento non trovata o disattivata oppure non esistono filiali associate all'agenzia", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/agencies/{agencyId}")
    public ResponseEntity<List<BranchResponse>> getBranchesByAgency(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int agencyId
    ){
        List<BranchResponse> list = branchService.getBranchesByAgency(agencyId);
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @Operation(
            summary = "GET BRANCHES BY COUNTRY",
            description = "Questo metodo restituisce la lista di filiali appartenenti ad una precisa nazione.",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco filiali restituito correttamente.", content = @Content(array = @ArraySchema(schema = @Schema(implementation =  BranchResponse.class)))),
                    @ApiResponse(responseCode="400", description="Il valore di countryId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nazione di riferimento non trovata o disattivata oppure non esistono filiali associate alla nazione.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/countries/{countryId}")
    public ResponseEntity<List<BranchResponse>> getBranchesByCountry(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero") short countryId
    ){
        List<BranchResponse> list = branchService.getBranchesByCountry(countryId);
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @Operation(
            summary = "UPDATE BRANCH",
            description = "Questo metodo serve per aggiornare sul database la filiale identificata dal proprio id.",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode="200", description="Filiale aggiornata con successo sul database.", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, city, address, vat o agencyId o id non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Agenzia di riferimento non trovata o disattivata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="La coppia di valori 'name/agencyId' oppure il vat sono già presenti sul database su una filiale diversa da quella che si sta aggiornando", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="422", description="L'agenzia di riferimento non è attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> update(
            @RequestBody @Valid BranchRequest req,
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int id
            ){
        return new ResponseEntity<>(branchService.update(req, id), HttpStatus.OK);
    }


    @Operation(
            summary = "ACTIVATE/DEACTIVATE BRANCH",
            description = "Questo metodo serve per modificare lo stato di una filiale da attivo a disattivo e viceversa.",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode="204", description="Stato della filiale aggiornato con successo.", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                    @ApiResponse(responseCode="400", description="L'id della filiale non presenta un valore valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> switchBranchStatus(
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int id
    ){
        branchService.switchBranchStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(
            summary = "GET BRANCH",
            description = "Questo metodo restituisce una filiale in base all'id.",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode="200", description="Filiale trovata.", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                    @ApiResponse(responseCode="400", description="L'id della filiale non presenta un valore valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale non trovata o disattivata.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranch(
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int id
    ){
        return new ResponseEntity<>(branchService.getBranch(id), HttpStatus.OK);
    }
}
