package com.odissey.country_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CountryDetailResponse extends CountryResponse{

    private boolean active;

    public CountryDetailResponse(String id, String name, String currency, boolean active) {
        super(id, name, currency);
        this.active = active;
    }
}
