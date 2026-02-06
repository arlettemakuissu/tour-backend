package com.odissey.country_service.service;

import com.odissey.country_service.configuration.RabbitConfig;
import com.odissey.country_service.dto.request.CountryRabbitMessage;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final RabbitTemplate rabbitTemplate;

    public CountryResponse create(CountryRequest countryRequest, int createdBy){
        String id = countryRequest.id().toUpperCase();
        String name = countryRequest.name();
        if(countryRepository.existsByIdOrName(id, name))
            throw new Exception409("Nazione già presente");
        // istanzio oggetto country (entità)
        Country country = new Country(
                id,
                name,
                countryRequest.currency(),
                createdBy,
                null
        );
        // persisto entità country
        countryRepository.save(country);

        // invio messaggio ad exchange fanout con le sole informazioni della country
        // che ho appena persistito. La routing key è vuota
        rabbitTemplate.convertAndSend(
                RabbitConfig.FANOUT_EXCHANGE,"",
                CountryRabbitMessage.fromEntityToRabbitMessage(country)
        );

        return new CountryResponse(country.getId(), country.getName(), country.getCurrency());
    }

    @Transactional
    public CountryResponse update(String id, CountryUpdateRequest countryUpdateRequest, int updatedBy){
        Country country = countryRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nazione non trovata con codice " + id));

        if(countryRepository.existsByNameAndIdNot(countryUpdateRequest.name(), id))
            throw new Exception409("Nazione già presente col nome " + countryUpdateRequest.name());

        country.setName(countryUpdateRequest.name());
        country.setCurrency(countryUpdateRequest.currency());
        country.setUpdatedBy(updatedBy);

        rabbitTemplate.convertAndSend(
                RabbitConfig.FANOUT_EXCHANGE,"",
                CountryRabbitMessage.fromEntityToRabbitMessage(country)
        );

        return new CountryResponse(country.getId(), country.getName(), country.getCurrency());
    }

    @Transactional
    public CountryDetailResponse switchStatus(String id, int updatedBy) {
        Country country = countryRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nazione non trovata con codice " + id));
        country.setActive(!country.isActive());
        country.setUpdatedBy(updatedBy);

        rabbitTemplate.convertAndSend(
                RabbitConfig.FANOUT_EXCHANGE,"",
                CountryRabbitMessage.fromEntityToRabbitMessage(country)
        );

        return new CountryDetailResponse(country.getId(), country.getName(), country.getCurrency(), country.isActive());
    }

    public List<CountryDetailResponse> getAll() {
        return countryRepository.getAll();
    }

    public List<CountryResponse> getActiveCountries() {
        return countryRepository.getActiveCountries();
    }
}
