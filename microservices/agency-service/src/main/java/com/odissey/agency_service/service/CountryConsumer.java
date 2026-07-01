package com.odissey.agency_service.service;

import com.odissey.agency_service.entity.Country;
import com.odissey.agency_service.exception.AgencyException;
import com.odissey.agency_service.exception.ErrMsg;
import com.odissey.agency_service.repository.AgencyRepository;
import com.odissey.agency_service.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryConsumer {

    private final CountryRepository countryRepository;
    private final AgencyRepository agencyRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "country_agency_queue")
    @Transactional
    public void consumeCountryUpdate(Country country){
        log.info(">>> Ricevuto messaggio da country-service: "+country.toString());
        // Recuperare la country attuale prima di aggiornarla
        Boolean isActive = countryRepository.isActive(country.getId());
        // A seguito di una modifica sul country-service,
        // propago questa modifica sull'entità Country di agency-service
        countryRepository.save(country);
        // Qualora venga disattivata la Country, vengono disattivate anche tutte le agencies
        // presenti in quella country
        if(isActive != null && isActive && !country.isActive())
            agencyRepository.massiveAgenciesDeactivation(country);
        if(isActive != null && !isActive && country.isActive())
            agencyRepository.massiveAgenciesActivation(country);
    }

}
