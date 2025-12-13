package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TourRequest {

    @NotNull(message = "La filiale è obbligatoria")
    @Min(value = 1, message = "L'identificativo della filiale non può essere un intero minore di 1")
    private int branchId;

    @NotNull(message = "La nazione in cui si svolge il tour è obbligatoria")
    @Min(value = 1, message = "L'identificativo della nazione non può essere un intero minore di 1")
    private short countryId;

    @NotBlank(message = "Il nome del tour è obbligatorio")
    @Size(min = 1, max = 255, message = "Il nome del tour deve essere almeno di un carattere e non più di 255")
    private String name;

    @NotBlank(message = "La descrizione del tour è obbligatoria")
    @Size(min = 10, max = 20480, message = "La descrizione del tour deve essere almeno di 10 caratteri e non può superare i 20 kb")
    private String description;

    @NotNull(message = "La data di partenza del tour è obbligatoria")
    @Future(message = "La data di partenza del tour deve essere nel futuro")
    private LocalDate startDate;

    @NotNull(message = "La data di rientro del tour è obbligatoria")
    @Future(message = "La data di rientro del tour deve essere nel futuro")
    private LocalDate endDate;

    @NotNull(message = "Il numero minimo di partecipanti è obbligatorio")
    @Min(value = 1, message = "Il numero minimo di partecipanti è 1")
    @Max(value = 127, message = "il numero minimo di partecipanti non può superare i 127")
    private int minPax;

    @NotNull(message = "Il numero massimo di partecipanti è obbligatorio")
    @Min(value = 1, message = "Il numero minimo di partecipanti è 1")
    @Max(value = 127, message = "Il numero massimo di partecipanti non può superare i 127")
    private int maxPax;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Digits(integer=5, fraction=2, message = "Il prezzo del tour non può superare il valore di 99999.99")
    @Positive(message = "il prezzo non può essere negativo o zero")
    private float price;
}
