package com.odissey.tour_service.service;

import com.odissey.tour_service.entity.Country;
import com.odissey.tour_service.entity.TourStatus;
import com.odissey.tour_service.exception.ErrMsg;
import com.odissey.tour_service.exception.TourException;
import com.odissey.tour_service.repository.AgencyRepository;
import com.odissey.tour_service.repository.CountryRepository;
import com.odissey.tour_service.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryConsumer {

    private final CountryRepository countryRepository;
    private final TourRepository tourRepository;
    private final AgencyRepository agencyRepository;

    @Value("${app.serviceUser}")
    private int serviceUser;

    @RabbitListener(queues = "country_tour_queue")
    @Transactional
    public void consumeCountryUpdate(Country country){
        log.info(">>> Ricevuto messaggio da country-service: "+country.toString());
        // Reeupcero lo stato della country prima dell'aggiornamento
        Boolean isActive = countryRepository.isActive(country.getId());
        // A seguito di una modifica sul country-service,
        // propago questa modifica sull'entità Country di tour-service
        countryRepository.save(country);

        if(isActive != null && isActive && !country.isActive()) {
            Set<TourStatus> statuses = Set.of(TourStatus.WORK_IN_PROGRESS, TourStatus.OPEN, TourStatus.SOLD_OUT);
            // Qualora venga disattivata la country meta di destinazione dei tour,
            // i relativi tour vengono cancellati
            tourRepository.massiveChangeStatusByCountry(
                    TourStatus.CANCELED_DUE_COUNTRY_INACTIVITY,
                    LocalDateTime.now(),
                    serviceUser,
                    statuses,
                    country
                    );
            // Qualora venga disattivata la country sede dell'agenzia che ha creato i tour,
            // i relativi tour vengono cancellati
            tourRepository.massiveChangeStatusByAgencyInCountry(
                    TourStatus.CANCELED_DUE_AGENCY_INACTIVITY,
                    LocalDateTime.now(),
                    serviceUser,
                    statuses,
                    country
            );
            // Disattivo le agencies la cui country è stata disattivata
            tourRepository.massiveAgencyChangeStatusByCountry(
                    country
            );

        }
        // Qualora la country venga RI-attivata, RI-attivo anche tutte le agencies
        // resdidenti in quella country
        if(isActive != null && !isActive && country.isActive()) {
            agencyRepository.massiveAgencyActivationByCountry(country.getId());
        }

    }

}
