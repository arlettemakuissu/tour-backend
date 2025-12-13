package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CustomerRequest extends UserRequest{
    @NotBlank(message = "L'indirizzo è obbligatorio")
    private String address;

    @NotBlank(message = "Il nome della città è obbligatorio")
    @Pattern(regexp = "^[\\p{L}\\s']+$", message = "Il nome può contenere solo caratteri, apostrofi e caratteri accentati")
    private String city;

    @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero")
    @NotNull(message = "L'identificativo della nazione è obbligatorio")
    private short countryId;



}
