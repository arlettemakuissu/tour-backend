package com.odissey.agency_service.dto.response;

import com.odissey.agency_service.entity.Agency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AgencyResponse {

    private int id;
    private String name;
    private String vat;
    private String address;
    private String city;
    private String countryName;
    private String currency;

    public static AgencyResponse fromEntityToDto(Agency agency){
        return new AgencyResponse(
                agency.getId(),
                agency.getName(),
                agency.getVat(),
                agency.getAddress(),
                agency.getCity(),
                agency.getCountry().getName(),
                agency.getCountry().getCurrency()
        );
    }
}
