package com.odissey.agency_service.dto.request;

import com.odissey.agency_service.entity.Agency;
import com.odissey.agency_service.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class AgencyRabbitMessage {
    private int id;
    private String name;
    private String vat;
    private boolean active;
    private String address;
    private String city;
    private Country country;

    public static AgencyRabbitMessage fromEntityToRabbitMessage(Agency agency){
        return new AgencyRabbitMessage(
                agency.getId(),
                agency.getName(),
                agency.getVat(),
                agency.isActive(),
                agency.getAddress(),
                agency.getCity(),
                agency.getCountry()
        );
    }
}
