package com.odissey.tour_service.service;

import com.odissey.tour_service.entity.Tour;
import com.odissey.tour_service.entity.TourStatus;
import com.odissey.tour_service.repository.TourRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulingService {

    private final TourRepository tourRepository;

    //@Scheduled(cron = "@daily") // ogni giorno a mezzanotte
    @Scheduled(cron = "0 0 9-13 * * *") // ogni giorno alle 9, 10, 11, 12 e 13
    @Transactional
    public void automaticChanceStatus(){
        Set<TourStatus> statuses = Set.of(TourStatus.OPEN, TourStatus.SOLD_OUT);
        List<Tour> list = tourRepository.findByStatusIn(statuses);
        LocalDate now = LocalDate.now();
        for(Tour tour : list){
            if(now.isAfter(tour.getStartDate())) {
                tour.setStatus(TourStatus.IN_PROGRESS);
                log.info(">>> Cambio stato per tour {}", tour.getId());
            }
        }
    }
}
