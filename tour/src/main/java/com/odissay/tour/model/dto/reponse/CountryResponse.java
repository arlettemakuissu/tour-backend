package com.odissay.tour.model.dto.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odissay.tour.model.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountryResponse {

    private short id;
    private String code;
    private String name;
    private String currency;

    public static CountryResponse fromEntityToDto(Country country){
        return new CountryResponse(
                country.getId(),
                country.getCode(),
                country.getName(),
                country.getCurrency()
        );
    }

    public  CountryResponse (short id,String name){

        this.id =id;
        this.name = name;

    }

}
