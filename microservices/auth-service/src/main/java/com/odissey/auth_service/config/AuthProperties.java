package com.odissey.auth_service.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth.jwt")
@Getter @Setter
@Validated
public class AuthProperties {

    @NotBlank
    private String issuer;

    @NotBlank
    private String audience;

    @NotBlank
    private String hmacSecret;

    @NotBlank
    private String rolesClaim;

    @NotNull @Positive
    private long accessTokenTtlSeconds;

    @NotNull @Positive
    private long refreshTokenTtlSeconds;
}
