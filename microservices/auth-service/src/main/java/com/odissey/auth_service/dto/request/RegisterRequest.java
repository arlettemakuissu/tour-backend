package com.odissey.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank @Size(min = 3, max = 20)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, max = 16)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$")
    private String password;

    @NotBlank
    private String role;

    private String displayName;
}
