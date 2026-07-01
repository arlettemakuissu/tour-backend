package com.odissey.tour_service.service;

import com.odissey.tour_service.entity.Agency;
import com.odissey.tour_service.entity.TourStatus;
import com.odissey.tour_service.repository.AgencyRepository;
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
public class AgencyConsumer {

    private final AgencyRepository agencyRepository;
    private final TourRepository tourRepository;

    @Value("${app.serviceUser}")
    private int serviceUser;

    @RabbitListener(queues = "agency_tour_queue")
    @Transactional
    public void consumeAgencyUpdate(Agency agency){
        log.info(">>> Ricevuto messaggio da agency-service: "+agency.toString());

        // Recupero lo stato dell'agency prima di aggiornarla
        Boolean isActive = agencyRepository.isActive(agency.getId());
        // A seguito di una modifica sul agency-service,
        // propago questa modifica sull'entità Agency di agency-service
        agencyRepository.save(agency);

        // qualora l'agency sia stata disattivata, cancello tutti i tour generati da quell'agency
        if(isActive != null && isActive && !agency.isActive()) {
            Set<TourStatus> statuses = Set.of(TourStatus.WORK_IN_PROGRESS, TourStatus.OPEN, TourStatus.SOLD_OUT);
            tourRepository.massiveTourChangeStatusByAgency(
                    TourStatus.CANCELED_DUE_AGENCY_INACTIVITY,
                    LocalDateTime.now(),
                    serviceUser,
                    statuses,
                    agency
            );
        }

    }

}
