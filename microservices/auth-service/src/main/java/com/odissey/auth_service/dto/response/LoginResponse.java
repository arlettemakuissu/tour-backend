package com.odissey.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {

    private String displayName;
    private String email;
    private String roles;
    private String jwt;
    private String refreshToken;
}
