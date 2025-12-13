package com.odissay.tour.model.dto.reponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.odissay.tour.model.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrancheReponse {


    // membri della classe Branch
    private Integer id;
    private String name;
    private String city;
    private String address;
    private String vat;

    // membri della classe Agency
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer agencyId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String agencyName;

    // membri della classe Country
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countryName;

    public BrancheReponse(Integer id, String name, String city, String vat, String countryName, String address) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.vat = vat;
        this.countryName = countryName;
        this.address = address;
    }

    public BrancheReponse(Integer id, String name, String vat, String address, String city, Integer agencyId, String agencyName) {
        this.id = id;
        this.name = name;
        this.vat = vat;
        this.address = address;
        this.city = city;
        this.agencyId = agencyId;
        this.agencyName = agencyName;
    }

    public static BrancheReponse fromEntityToDto(Branch branch){
        return new BrancheReponse(
                branch.getId(),
                branch.getName(),
                branch.getCity(),
                branch.getAddress(),
                branch.getVat(),
                branch.getAgency().getId(),
                branch.getAgency().getName(),
                branch.getAgency().getCountry().getName()
        );
    }
}
