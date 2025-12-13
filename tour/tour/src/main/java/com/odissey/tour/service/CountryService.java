package com.odissey.tour.service;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.model.dto.request.CountryRequest;
import com.odissey.tour.model.dto.response.CountryResponse;
import com.odissey.tour.model.entity.Country;
import com.odissey.tour.repository.CountryRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryResponse save(CountryRequest req){
        String code = req.getCode().toUpperCase().trim();
        String name = req.getName().trim();
        // verificare che non esista già una country con code oppure name passati nella request
        if(countryRepository.existsByCode(code)){
            throw new Exception409("Una nazione con codice "+code+" è già presente a sistema.");
        }
        if(countryRepository.existsByName(name)){
            throw new Exception409("Una nazione con nome "+name+" è già presente a sistema.");
        }
        // istanzio oggetto Country
        Country country = new Country(code, name, req.getCurrency().trim());
        // persisto su db oggetto Country
        countryRepository.save(country);
        // mi faccio restituire una CountryResponse
        return CountryResponse.fromEntityToDto(country);

    }

    public List<CountryResponse> getActiveCountries(){
        List<CountryResponse> list = countryRepository.findAllActiveCountries();
        return list;
    }

    public List<CountryResponse> getCountries(){
        List<CountryResponse> list = countryRepository.findAllCountries();
        return list;
    }

    @Transactional
    public CountryResponse update(short id, CountryRequest req){
        // query per recuperare la nazione da aggiornare in base all'id
        Country country = countryRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nazione con id "+id+" non trovata"));
        // setto i nuovi valori
        String code = req.getCode().toUpperCase().trim();
        String name = req.getName().trim();
        if(countryRepository.existsByCodeAndIdNot(code, id))
            throw new Exception409("Una nazione col codice "+code+" esiste già a sistema.");
        if(countryRepository.existsByNameAndIdNot(name, id))
            throw new Exception409("Una nazione col nome "+name+" esiste già a sistema.");
        country.setCode(code);
        country.setName(name);
        country.setCurrency(req.getCurrency().trim());
        // il salvataggio sul db viene fatto in automatico grazie all'anotazione @Transactional
        return CountryResponse.fromEntityToDto(country);
    }


    @Transactional
    public void switchCountryStatus(short id) {
        // query per recuperare la nazione da aggiornare in base all'id
        Country country = countryRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nazione con id "+id+" non trovata"));
        country.setActive(!country.isActive());
    }

    public CountryResponse getCountry(short id){
        return countryRepository.findCountry(id)
                .orElseThrow(()-> new Exception404("Nazione con id "+id+" non trovata"));
    }
}
