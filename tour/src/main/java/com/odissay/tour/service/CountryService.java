package com.odissay.tour.service;

import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.dto.request.CountryRequest;
import com.odissay.tour.model.entity.Country;
import com.odissay.tour.model.dto.reponse.CountryResponse;
import com.odissay.tour.repository.CountryRepository;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryRepository countryRepository;
    private static final String CACHE_COUNTRIES_ALL = "countries:all";
    private static final String CACHE_COUNTRY_DETAIL = "country :detail";
    private static final String CACHE_COUNTRIES_ACTIVE = "countries:active";

   @Caching(evict = {
           @CacheEvict(cacheNames = CACHE_COUNTRIES_ALL,allEntries = true),
           @CacheEvict(cacheNames = CACHE_COUNTRIES_ACTIVE,allEntries = true)


           }
   )
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
    @Cacheable(cacheNames =  CACHE_COUNTRIES_ACTIVE )
    public List<CountryResponse> getActiveCountries(){

   return  countryRepository.findAllActiveCountries();


    }
    @Cacheable(cacheNames = CACHE_COUNTRIES_ALL)
    public List<CountryResponse> findAllCountries(){

        return countryRepository.findAllCountries();



    }

     @Transactional // tiene aperta la transaction fine tanto che il metodo non si conclude
     @Caching(put= @CachePut(cacheNames = CACHE_COUNTRY_DETAIL, key="#id"),
        evict  = {
           @CacheEvict(cacheNames = CACHE_COUNTRIES_ALL,allEntries = true),
           @CacheEvict(cacheNames = CACHE_COUNTRIES_ACTIVE , allEntries = true)
       })
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
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CACHE_COUNTRIES_ACTIVE,allEntries = true),
                    @CacheEvict(cacheNames = CACHE_COUNTRIES_ALL,allEntries = true),
                    @CacheEvict(cacheNames = CACHE_COUNTRY_DETAIL,key= "#id")
            }
    )
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

   @Cacheable(cacheNames = CACHE_COUNTRY_DETAIL,key = "#id")
   public CountryResponse getCountry(short id){

        return countryRepository.findCountry(id)
                .orElseThrow(() -> new Exception404("nazione  con id  "+id+"  non trovato"));
   }
}
