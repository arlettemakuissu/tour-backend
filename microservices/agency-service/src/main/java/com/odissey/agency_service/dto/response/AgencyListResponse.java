package com.odissey.agency_service.dto.response;

import com.odissey.agency_service.entity.Agency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AgencyListResponse extends AgencyActiveListResponse{

    private boolean active;

    public AgencyListResponse(int id, String name, String address, String city, String countryCode, boolean active) {
        super(id, name, address, city, countryCode);
        this.active = active;
    }

    public static AgencyListResponse fromEntityToDto(Agency agency){
        return new AgencyListResponse(
                agency.getId(),
                agency.getName(),
                agency.getAddress(),
                agency.getCity(),
                agency.getCountry().getId(),
                agency.isActive()
        );
    }
}
