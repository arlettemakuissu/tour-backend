package com.odissay.tour.controller;


import com.odissay.tour.model.dto.reponse.CommentResponse;
import com.odissay.tour.model.dto.reponse.CustomErrorResponse;
import com.odissay.tour.model.dto.request.CommentRequest;
import com.odissay.tour.service.CommentService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "CREATE COMMENT",
            description = "Questo metodo serve alla scrittura di un commento da parte di un customer ad un tour che si è concluso.",
            tags = {"Comment"},
            responses = {
                    @ApiResponse(responseCode="201", description="Creazione commento eseguita con successo.", content = @Content(schema = @Schema(implementation = CommentResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di customerId, tourId, content, refereTo non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour o customer o commento di riferimento non trovati.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<CommentResponse> create(@RequestBody @Valid CommentRequest req, @AuthenticationPrincipal UserDetails userDetails){

    return new ResponseEntity<>(commentService.create(req,userDetails),HttpStatus.CREATED);


    }

    @Operation(
            summary = "GET COMMENTS BY TOUR",
            description = "Questo metodo serve a ottenere la lista di commenti ad un determinato tour.",
            tags = {"Comment"},
            responses = {
                    @ApiResponse(responseCode="200", description="Lista commenti generata con successo.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))),
                    @ApiResponse(responseCode="400", description="Il tourId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nessun commento è stato scritto per il tour in oggetto.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/{tourId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTour(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un numero intero positivo.") int tourId
    ){
        List<CommentResponse> list = commentService.getCommentsByTour(tourId);
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @Operation(
            summary = "CENSOR COMMENT",
            description = "Questo metodo serve a censurare un commento: il commento censurato sarà sostituito a video da una serie di asterischi.",
            tags = {"Comment"},
            responses = {
                    @ApiResponse(responseCode="200", description="Commento censurato con successo.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))),
                    @ApiResponse(responseCode="400", description="Il commentId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Commento non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PatchMapping("/{commentId}")
     public ResponseEntity<String> censorComment(@PathVariable @Min(value = 1, message = "L'id del commento deve essere un numero intero positivo.") int commentId){

       return  new ResponseEntity<>(commentService.censorComment(commentId), HttpStatus.OK);
     }
    
}