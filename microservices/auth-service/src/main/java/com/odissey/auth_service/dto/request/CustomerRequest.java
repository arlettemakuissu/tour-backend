package com.odissey.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CustomerRequest extends RegisterRequest{

    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotNull
    private boolean receiveNewsletter;
    @NotNull
    private boolean acceptServiceTerms;
}
