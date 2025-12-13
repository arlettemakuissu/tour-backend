package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CountryRequest {

    @NotBlank(message = "Il codice nazione è obbligatorio e non può essere formato da soli spazi")
    @Size(min=2, max=2, message = "Il codice nazione deve essere obbligatoriamnte di 2 caratteri")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Il codice può contenere solo caratteri")
    private String code;

    @NotBlank(message = "Il nome nazione è obbligatorio e non può essere formato da soli spazi")
    @Size(min=1, max=255, message = "Il nome nazione deve essere almeno di un carattere e non più lungo di 255 caratteri")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Il codice può contenere solo caratteri")
    private String name;

    @NotBlank(message = "Il codice nazione è obbligatorio e non può essere formato da soli spazi")
    @Size(min=1, max=255, message = "Il nome della valuta deve essere almeno di un carattere e non più lungo di 255 caratteri")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Il codice può contenere solo caratteri")
    private String currency;
}
