package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
   @NotBlank(message ="lo username è obbligatorio")
   @Size(max= 30,min = 3 ,message = "la lunghezza deve compresa tra 3 e 30 caractteri")
   private String username;

    @NotBlank(message ="l'email è obbligatorio")
    private String email;
    @NotBlank(message ="il cognome è obbligatorio")
    @Size(max= 255,min = 3 ,message = "la lunghezza deve compresa tra 3 e 255 caractteri" )
    private String firstname;
    @NotBlank(message ="il nome è obbligatorio")
    @Size(max= 255,min = 3 ,message = "la lunghezza deve compresa tra 3 e 255 caractteri")
    private String lastname;
    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter and one special character")
   private String password;



}
