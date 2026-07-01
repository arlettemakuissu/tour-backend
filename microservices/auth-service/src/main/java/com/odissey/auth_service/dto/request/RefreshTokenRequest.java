package com.odissey.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {

    @NotBlank @Size(min = 36, max = 36)
    private String refreshTokenId;
}
