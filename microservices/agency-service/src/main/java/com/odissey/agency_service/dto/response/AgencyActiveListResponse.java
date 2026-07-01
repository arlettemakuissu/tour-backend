package com.odissey.agency_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AgencyActiveListResponse {

    private int id;
    private String name;
    private String address;
    private String city;
    private String countryCode;
}
