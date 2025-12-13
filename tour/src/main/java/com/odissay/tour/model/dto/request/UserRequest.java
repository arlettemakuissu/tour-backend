package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRequest {
    @NotBlank(message = "Lo username è obbligatorio")
    @Size(max = 30, min = 3, message = "La lunghezza deve essere compresa tra 3 e 30 cararatteri")
    private String username;

    @NotBlank(message = "L'email' è obbligatoria")
    private String email;

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 255, min = 2, message = "La lunghezza deve essere compresa tra 2 e 255 cararatteri")
    @Pattern(regexp = "^[\\p{L}\\s']+$", message = "Il nome può contenere solo caratteri, apostrofi e caratteri accentati")
    private String firstname;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 255, min = 2, message = "La lunghezza deve essere compresa tra 2 e 255 cararatteri")
    @Pattern(regexp = "^[\\p{L}\\s']+$", message = "Il nome può contenere solo caratteri, apostrofi e caratteri accentati")
    private String lastname;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, max = 16, message = "La password deve essere lunga almeno 8 caratteri e non più di 16")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "La password deve contenere almeno un numero, un carattere minuscolo, un carattere maiuscolo e almeno un  carattere sepciale tra questi @#$£%^&+=!")
    private String password;



}
