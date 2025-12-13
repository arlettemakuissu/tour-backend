package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerUpdateRequest extends UserUpdateRequest{

    @NotBlank(message = "l indirizzo è obbligatorio")
    private String address;
    @NotBlank
    @Pattern(regexp = "^[\\p{L}\\s']+$", message = "Il nome può contenere solo caratteri e apostrofi")
    private String city;
    @Min(value=1, message = "L'id della nazione deve essere un numero intero maggiore di 0.")

    @NotNull
    private short countryId;


}
