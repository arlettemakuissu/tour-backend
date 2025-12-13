package com.odissay.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRoleRequest extends UserRequest {
    @NotBlank(message = "il role è obbligatorio")
    private String role;
}
