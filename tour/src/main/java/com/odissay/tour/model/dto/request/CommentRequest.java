package com.odissay.tour.model.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequest {


    @NotNull
    @Min(value= 1 , message="l'id del tour deve essere un numero intero e positivo")
    private int tourId;

    @NotBlank(message = "il testo del commento è obbligattorio .")
    @Size(min =2,max = 255,message = "la lunghezza del commento è compressa tra 2 e 255")
    private String content;

    @Min(value=1,message = "l'id del commento di riferimento deve essere un numero intero positivo")
    private Integer refererTo;

}
