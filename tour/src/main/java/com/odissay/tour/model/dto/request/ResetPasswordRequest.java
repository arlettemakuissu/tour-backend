package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    private String email;

    @NotBlank(message = "Il codice otp è obbligatorio")
    @Size(min = 6, max = 6, message = "Otp non valido")
    private String otpCode;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 8, max = 16, message = "La password deve essere lunga almeno 8 caratteri e non più di 16")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "La password deve contenere almeno un numero, un carattere minuscolo, un carattere maiuscolo e almeno un  carattere sepciale tra questi @#$£%^&+=!")
    private String password1;

    @NotBlank(message = "La ripetizione della nuova password è obbligatoria")
    @Size(min = 8, max = 16, message = "La password deve essere lunga almeno 8 caratteri e non più di 16")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "La password deve contenere almeno un numero, un carattere minuscolo, un carattere maiuscolo e almeno un  carattere sepciale tra questi @#$£%^&+=!")
    private String password2;
}
