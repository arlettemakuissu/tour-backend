package com.odissey.tour_ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TourRequest {


    @NotBlank(message = "la destinazioone è obbligattoria")
    private String destinazione; // nome del luogoda visitare
    @Positive(message = "il tour deve durare almeno un giorno")
    @Max(value =30,message = "la durata del tour non pou superare 30")
    private int duration;
    //private int minPax;
    @NotBlank(message = "specificare un livello di spessa")
    private String budget;// economico,lussuoso

    @NotBlank(message = "specificare un tipologia  di tour")
    private String type;// tipo di viaggio


}
