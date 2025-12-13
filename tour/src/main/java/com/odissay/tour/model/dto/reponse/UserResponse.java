package com.odissay.tour.model.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class UserResponse {

    private int id;
    private String email;
    private String firstname;
    private String lastname;


}
