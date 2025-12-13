package com.odissay.tour.controller;


import com.odissay.tour.model.dto.reponse.*;
import com.odissay.tour.model.dto.request.TourRequest;
import com.odissay.tour.service.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAnyAuthority;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tours") //mi cercherà tutto nella tabella countries
@Validated

public class TourController {

    private final TourService tourService;


    @Operation(
            summary = "CREATE tour",
            description = "Questo metodo serve a inserire una nuovo tour sul database",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour creato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, description, country, branch, startDate, endDate, minPax, maxPax o price non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )


    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse>created(
            @RequestBody @Valid TourRequest req){
        System.out.println( req.getMinPax());

        TourDetailResponse tour = tourService.create(req);

        return  new ResponseEntity<>(tour , HttpStatus.CREATED);
    }

@GetMapping
@PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
public ResponseEntity<TourResponsePaginated>getAllTours(

        @RequestParam(required = false )  @Min(value =1,message = "l'id della filliate deve essere un numero intero")Integer branchId,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam (defaultValue = "0") int pageNumber,
        @RequestParam (defaultValue = "startDate") String  sortBy,
        @RequestParam (defaultValue = "Desc") String direction

){
    System.out.println("hello88888888888888888888888888888888888888888888888888888888" );

    TourResponsePaginated  page = tourService.getAllTours(branchId,pageSize,pageNumber,sortBy,direction);
         if(page.getData().isEmpty()){
             return new ResponseEntity<>(page,HttpStatus.NOT_FOUND);
         }
       return new ResponseEntity<>(page,HttpStatus.OK);




}

    @GetMapping("/filtered")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public ResponseEntity<TourResponsePaginated>getFilteredTours(

            @RequestParam(required = false )  @Min(value =1,message = "l'id della filliate deve essere un numero intero")Integer branchId,
            // status,nazione,datapartenza,prezzo,media,keyword(in titolo o description)

            @RequestParam(required = false )String status,
            @RequestParam(required = false )Short countryId,
            @RequestParam(required = false )LocalDate startDate,
            @RequestParam(required = false )LocalDate endDate,
            @RequestParam(defaultValue = "0.0" )@PositiveOrZero(message ="il prezzo minimo non puo essere negativo") Float minPax ,
            @RequestParam(required = false )Float maxPax,
            @Parameter(description = "se valorizzato restituisce il tour che non hai mai recevuto una rate,almeno altri filtri")
            @RequestParam(required = false)@PositiveOrZero(message = "se valorizzato restituisce il tour che non hai mai recevuto una rate,almeno altri filtri") Double avg,
            @RequestParam(required=false) String  keyword,
            @RequestParam(defaultValue = "false") boolean isCaseSensitive,
            @RequestParam(defaultValue = "false") boolean isExactMatch,
            @RequestParam(defaultValue = "10")  @Min(value = 1, message = "Il numero di elementi per pagina deve essere un un numero intero positivo maggiore di zero.") int pageSize,
            @RequestParam (defaultValue = "0") @PositiveOrZero(message = "Il numero della pagina da cui partire deve essere un numero intero positivo o zero (prima pagina)") int pageNumber,
            @RequestParam(defaultValue = "startDate") String sortBy,

            @RequestParam (defaultValue = "Desc") String direction

    ){

        TourResponsePaginated  page = tourService.getFilteredTours(branchId,status,countryId,startDate,endDate,minPax,maxPax,avg,keyword,isCaseSensitive,isExactMatch,pageSize,pageNumber,sortBy,direction);

        if(page.getData().isEmpty()){
            return new ResponseEntity<>(page,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(page,HttpStatus.OK);

    }

    @Operation(
            summary = "UPDATE TOUR",
            description = "Questo metodo serve ad aggiornare un tour. Un tour può essere modificato finché è in stato WORK_IN_PROGRESS.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour modificato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di id, name, description, country, branch, startDate, endDate, minPax, maxPax o price non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<TourDetailResponse> update(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") int id,
            @RequestBody @Valid TourRequest req
    ){
       return new ResponseEntity<>(tourService.update(id,req),HttpStatus.OK);



    }

    @Operation(
            summary = "CANCEL TOUR",
            description = "Questo metodo serve per cancellare un tour d'ufficio. Azione permessa solo all'ADMIN. \n" +
                    "Prevede l'invio di una notifica via mail agli eventuali partecipanti qualora il tour non fosse più in status WORK_IN_PROGRESS. \n" +
                    "Se il tour è in stato EXPIRED non può essere cancellato.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour modificato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di id, name, description, country, branch, startDate, endDate, minPax, maxPax o price non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )

    @PatchMapping("/{id}/canceled")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<TourDetailResponse>cancelTour(
            @PathVariable @Min(value = 1,message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") int id
    ) {
    System.out.println("hellooooooooooooooooooooooooooooooooooo");
     return new ResponseEntity<>(tourService.cancelTour(id),HttpStatus.OK);

    }

    @PatchMapping("/{id}/{status}")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse>changeTourStatus(
            @PathVariable @Min(value = 1,message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") int id,
            @PathVariable @NotBlank ( message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") String status


    ) {
     System.out.println("OOOOOOOOOOOO");

        return new ResponseEntity<>(tourService.changeTourStatus(id,status),HttpStatus.OK);

    }

    /**
     * prossimo step -> rimborso in caso di cancellazione o ripensamento del customer
     * in quest'ultimo caso rimuovere la prenotazione a patto che il ripensamento avvenga entro 5 giorni precedenti la data di partenza del tour
     * e rimborsare solo il 60% del versato
     *
     * STEP :
     * 1 verificare se esiste la prenotazione
     * trovare la somma dei pagamenti effetueti per il toul al quale a prenotato
     * rimborso i pagamenti del 60% solo se la disdetta solo entro 5 gioni della partenza.
     */

    @PutMapping
    @PreAuthorize("")
    public ResponseEntity<String> earlyCancellation(@PathVariable @Min (value = 1 ,message = "l'id del customer è obbligatorio e deve essere un numero intero") int customerId,
                                                    @PathVariable @Min (value = 1 ,message = "l'id del tour è obbligatorio e deve essere un nemro intero e positivo") int tourId){

        return new ResponseEntity<>(tourService.earlyCancellation(tourId,customerId),HttpStatus.OK);
    }


}
