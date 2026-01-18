package com.odissey.country_service.service;

import com.odissey.country_service.dto.request.CountryRequest;
import com.odissey.country_service.dto.request.CountryUpdateRequest;
import com.odissey.country_service.dto.response.CountryDetailResponse;
import com.odissey.country_service.dto.response.CountryResponse;
import com.odissey.country_service.entity.Country;
import com.odissey.country_service.exception.Exception400;
import com.odissey.country_service.exception.Exception404;
import com.odissey.country_service.exception.Exception409;
import com.odissey.country_service.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryResponse create(CountryRequest countryRequest, int userId){
        String id = countryRequest.id().toUpperCase();
        String name = countryRequest.name();
        if(countryRepository.existsByIdOrName(id, name))
            throw new Exception409("Nazione già presente");
        Country country = new Country(
                id,
                name,
                countryRequest.currency(),
                userId,
                null
        );

        countryRepository.save(country);
        return new CountryResponse(country.getId(), country.getName(), country.getCurrency());
    }

    @Transactional
    public CountryResponse update(String id, CountryUpdateRequest countryUpdateRequest, int userId){
        Country country = countryRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nazione non trovata con codice " + id));

        if(countryRepository.existsByNameAndIdNot(countryUpdateRequest.name(), id))
            throw new Exception409("Nazione già presente col nome " + countryUpdateRequest.name());

        country.setName(countryUpdateRequest.name());
        country.setCurrency(countryUpdateRequest.currency());
        country.setUpdatedBy(userId);

        return new CountryResponse(country.getId(), country.getName(), country.getCurrency());
    }

    @Transactional
    public CountryDetailResponse switchStatus(String id, int userId) {
        Country country = countryRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nazione non trovata con codice " + id));
        country.setActive(!country.isActive());
        country.setUpdatedBy(userId);
        return new CountryDetailResponse(country.getId(), country.getName(), country.getCurrency(), country.isActive());
    }

    public List<CountryDetailResponse> getAll() {
        return countryRepository.getAll();
    }

    public List<CountryResponse> getActiveCountries() {
        return countryRepository.getActiveCountries();
    }
}
