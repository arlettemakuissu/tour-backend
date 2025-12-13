package com.odissay.tour.model.dto.request;

import com.odissay.tour.model.entity.emurator.VoucherType;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class VoucherRequest {

    @NotBlank(message =  "il nome e il cognome della persona è obbligattorio ")
    private String emittedBy;
    @NotNull(message =  "l'id del customer è obbligatorio ")
    @Min(value =1,message = " deve essere un numero positivo e maggiore di zero")
    private int customerId;

    @NotNull(message =  "prezzo è obbligatorio ")
    @Digits(integer =4,fraction = 2)
    @Positive(message = "il prezzo deve essere un numero positivo")
    private float price;
    @NotBlank(message = "la causale del vaucher deve essere presente")
    private String type;
}
