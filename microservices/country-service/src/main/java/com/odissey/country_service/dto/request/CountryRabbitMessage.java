package com.odissey.country_service.dto.request;

import com.odissey.country_service.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CountryRabbitMessage {
    private String id;
    private String name;
    private String currency;
    private boolean active;

    public static CountryRabbitMessage fromEntityToRabbitMessage(Country country){
        return new CountryRabbitMessage(
             country.getId(),
             country.getName(),
             country.getCurrency(),
             country.isActive()
        );
    }
}
