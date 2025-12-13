package com.odissay.tour.model.dto.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerDetailResponse extends CustomerResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "")
    private LocalDateTime lastLogin;

    public CustomerDetailResponse(int id, String email, String firstname, String lastname, String address, String city, String countryName, LocalDateTime lastLogin) {
        super(id, email, firstname, lastname, address, city, countryName);
        this.lastLogin = lastLogin;
    }
}
