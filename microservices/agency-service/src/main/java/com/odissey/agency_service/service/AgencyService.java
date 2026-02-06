package com.odissey.agency_service.service;

import com.odissey.agency_service.configuration.RabbitConfig;
import com.odissey.agency_service.dto.request.AgencyRabbitMessage;
import com.odissey.agency_service.dto.request.AgencyRequest;
import com.odissey.agency_service.dto.response.AgencyActiveListResponse;
import com.odissey.agency_service.dto.response.AgencyListResponse;
import com.odissey.agency_service.dto.response.AgencyResponse;
import com.odissey.agency_service.entity.Agency;
import com.odissey.agency_service.entity.Country;
import com.odissey.agency_service.exception.AgencyException;
import com.odissey.agency_service.exception.ErrMsg;
import com.odissey.agency_service.repository.AgencyRepository;
import com.odissey.agency_service.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final CountryRepository countryRepository;
    private final RabbitTemplate rabbitTemplate;

    public AgencyResponse create(AgencyRequest agencyRequest, int createdBy){
        if(agencyRepository.existsByVatAndCountryActiveTrue(agencyRequest.vat()))
            throw new AgencyException(ErrMsg.VAT_ALREADY_PRESENT);
        Country country = countryRepository.findByIdAndActiveTrue(agencyRequest.countryCode())
                .orElseThrow(() -> new AgencyException(ErrMsg.COUNTRY_NOT_FOUND));
        Agency agency = new Agency(
                agencyRequest.name(),
                agencyRequest.vat(),
                agencyRequest.address(),
                agencyRequest.city(),
                country,
                createdBy,null
        );
        agencyRepository.save(agency);

        rabbitTemplate.convertAndSend(
                RabbitConfig.AGENCY_EXCHANGE,RabbitConfig.AGENCY_ROUTING_KEY,
                AgencyRabbitMessage.fromEntityToRabbitMessage(agency)
        );
        return AgencyResponse.fromEntityToDto(agency);
    }

    @Transactional
    public AgencyResponse update(int id, AgencyRequest agencyRequest, int updatedBy){
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(()-> new AgencyException(ErrMsg.AGENCY_NOT_FOUND));
       if(agencyRepository.existsByVatAndIdNotAndCountryActiveTrue(agencyRequest.vat(), id))
           throw new AgencyException(ErrMsg.VAT_ALREADY_PRESENT);
       Country country = countryRepository.findByIdAndActiveTrue(agencyRequest.countryCode())
                       .orElseThrow(()-> new AgencyException(ErrMsg.COUNTRY_NOT_FOUND));

       agency.setName(agencyRequest.name());
       agency.setVat(agencyRequest.vat());
       agency.setAddress(agencyRequest.address());
       agency.setCity(agencyRequest.city());
       agency.setCountry(country);
       agency.setUpdatedBy(updatedBy);

        rabbitTemplate.convertAndSend(
                RabbitConfig.AGENCY_EXCHANGE,RabbitConfig.AGENCY_ROUTING_KEY,
                AgencyRabbitMessage.fromEntityToRabbitMessage(agency)
        );

       return AgencyResponse.fromEntityToDto(agency);
    }

    public List<AgencyListResponse> getAll(){
        return agencyRepository.getAll();
    }

    public List<AgencyActiveListResponse> getActive(){
        return agencyRepository.getActive();
    }

    @Transactional
    public AgencyListResponse switchStatus(int id, int updatedBy){
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(()-> new AgencyException(ErrMsg.AGENCY_NOT_FOUND));
        agency.setActive(!agency.isActive());
        agency.setUpdatedBy(updatedBy);

        rabbitTemplate.convertAndSend(
                RabbitConfig.AGENCY_EXCHANGE,RabbitConfig.AGENCY_ROUTING_KEY,
                AgencyRabbitMessage.fromEntityToRabbitMessage(agency)
        );

        return AgencyListResponse.fromEntityToDto(agency);
    }
}
