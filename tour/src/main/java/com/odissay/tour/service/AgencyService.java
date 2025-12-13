package com.odissay.tour.service;

import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.dto.reponse.AgencyResponse;
import com.odissay.tour.model.dto.request.AgencyRequest;
import com.odissay.tour.model.entity.Agency;
import com.odissay.tour.model.entity.Country;
import com.odissay.tour.repository.AgencyRepository;
import com.odissay.tour.repository.CountryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final CountryRepository countryRepository;


    public AgencyResponse save(AgencyRequest req){
       // verifico l'esitenza della countr e istanzio un oggetto country
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(() -> new Exception404("nazion non trovato con id" + req.getCountryId()));
        Agency agency = new Agency(
                req.getName(),
                req.getCity(),
                req.getAddress(),
                req.getVat() ,
                country

        );
        agencyRepository.save(agency);
        return AgencyResponse.fromEntityToDto(agency);

    }


    public List<AgencyResponse> findAllAgencies(){

        List<AgencyResponse>  list =  agencyRepository.findAllAgencies();

        if(list.isEmpty()){

            throw new Exception404("agenzia non trovato");
             }
             return list;
        }

    public AgencyResponse getAgency(int id){
        AgencyResponse agencyResponse = agencyRepository.findAgency(id)
                .orElseThrow(()-> new Exception404("Nessuna agenzia trovata con id "+id));
        return agencyResponse;

    }

    public List<AgencyResponse> getAgenciesByCountry(short countryId) {
        List<AgencyResponse> list = agencyRepository.findAgenciesByCountry(countryId);
        if (list.isEmpty()) {
            throw new Exception404("Nessuna agenzia trovata per la nazione inserita avente id: " + countryId);
        }
        return list;
    }

    public AgencyResponse update(int id, AgencyRequest req){
        // verificare l'esistenza della country e istanziarne un oggetto
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+req.getCountryId()));
        // verificare l'esistenza dell'agency e istanziarne un oggetto
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(()-> new Exception404("Agenzia non trovata con id "+id));
        // verificare che non esista un'altra agency con lo stesso VAT in quanto è un valore UNIQUE
        if(agencyRepository.existsByVatAndIdNot(req.getVat().trim(), id))
            throw new Exception409("Un'agenzia con lo stesso VAT è già presente a sistema");
        // settare i nuovi valori
        agency.setName(req.getName().trim());
        agency.setCity(req.getCity().trim());
        agency.setAddress(req.getAddress().trim());
        agency.setVat(req.getVat().trim());
        agency.setCountry(country);

        agencyRepository.save(agency);

        return AgencyResponse.fromEntityToDto(agency);
    }


    public void switchAgencyStatus(int id){
        // verificare l'esistenza dell'agency e istanziarne un oggetto
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(()-> new Exception404("Agenzia non trovata con id "+id));
        agency.setActive(!agency.isActive());
        agencyRepository.save(agency);
    }


}


