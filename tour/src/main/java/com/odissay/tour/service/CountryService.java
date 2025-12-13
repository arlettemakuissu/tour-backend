package com.odissay.tour.service;

import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.dto.request.CountryRequest;
import com.odissay.tour.model.entity.Country;
import com.odissay.tour.model.dto.reponse.CountryResponse;
import com.odissay.tour.repository.CountryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryResponse save(CountryRequest req){
        // verificare che non esista già una country con code oppure name passati nella request
        String code = req.getCode().toUpperCase().trim();
        String name = req.getName().toUpperCase().trim();

        if(countryRepository.existsByCodeOrName(code, name)){
            throw new Exception409("Una nazione con codice "+upper(req.getCode()+" o nome "+req.getName().trim()+" è già presente"));
        } else {
            // istanzio oggetto Country
            Country country = new Country(code, name, req.getCurrency().trim());
            // persisto su db oggetto Country
            countryRepository.save(country);
            // mi faccio restituire una CountryResponse
            return CountryResponse.fromEntityToDto(country);
        }
    }

    private String upper(String s){
        if(s != null)
            return s.toUpperCase().trim();
        return s;
    }

    public List<CountryResponse> getActiveCountries(){

   List<CountryResponse> list = countryRepository.findAllActiveCountries();

      return list;

    }

    public List<CountryResponse> findAllCountries(){

        List<CountryResponse> list = countryRepository.findAllCountries();

        return list;

    }

    @Transactional // tiene aperta la transaction fine tanto che il metodo non si conclude
    public CountryResponse update (short id,CountryRequest req){
        // Query per ricuperare la nazione da aggiunare in base a id

       Country country = countryRepository.findById(id)
               .orElseThrow(() -> new Exception404("nazione con id non trovato"));

       country.setCode(req.getCode());
       country.setName(req.getName());
       country.setCurrency(req.getCurrency());

      return CountryResponse.fromEntityToDto(country);


    }
    @Transactional
    public String switchCountryStatus ( short id){

       Country country = countryRepository.findById(id)
               .orElseThrow(() -> new Exception404("nazione con id non trovato"));

        System.out.println(country.getCode().length());
        if(country.isActive()){
            country.setActive(false);
        }else{
            country.setActive(true);
        }

        //country.setActive(!country.isActive());
       return "stato di modificazione della nazione";
   }
   public CountryResponse getCountry(short id){

        return countryRepository.findCountry(id)
                .orElseThrow(() -> new Exception404("nazione  con id  "+id+"  non trovato"));
   }
}
