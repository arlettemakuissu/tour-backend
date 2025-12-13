package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class AgencyRequest {

    @NotBlank
    private String name;
    @NotBlank(message="la cità è obbligatorio e non puo contenere solo spazi")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$", message = "Il codice deve contenere solo due caratteri")
    private String city;
    @NotBlank (message="l adresse è obbligatorio e non puo contenere solo spazi")
    private String address;
    @NotBlank (message="la VAT(value Added Tax) è obbligatorio e non puo contenere solo spazi")
    private String vat;

    @Min(value=1 ,message="l'id della nazione è in numero intero > di 0 ")
    private short countryId;

}
