package com.odissey.tour_service.service;

import com.odissey.tour_service.dto.request.BookingsTourRequest;
import com.odissey.tour_service.dto.request.TourRequest;
import com.odissey.tour_service.dto.response.*;
import com.odissey.tour_service.entity.Tour;
import com.odissey.tour_service.entity.TourStatus;
import com.odissey.tour_service.exception.ErrMsg;
import com.odissey.tour_service.exception.TourException;
import com.odissey.tour_service.repository.AgencyRepository;
import com.odissey.tour_service.repository.CountryRepository;
import com.odissey.tour_service.repository.HeroImageRepository;
import com.odissey.tour_service.repository.TourRepository;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {

    private final TourRepository tourRepository;
    private final CountryRepository countryRepository;
    private final AgencyRepository agencyRepository;
    private final HeroImageRepository heroImageRepository;


    public TourDetailResponse create(TourRequest tourRequest, int createdBy){
        short maxPax = tourRequest.maxPax();
        short minPax = tourRequest.minPax();
        LocalDate startDate = tourRequest.startDate();
        LocalDate endDate = tourRequest.endDate();

        if(minPax > maxPax)
            throw new TourException(ErrMsg.MINPAX_ERROR);

        if(startDate.isAfter(endDate))
            throw new TourException(ErrMsg.STARTDATE_ERROR);

        Tour tour = new Tour(
                tourRequest.name(),
                tourRequest.description(),
                startDate,
                endDate,
                minPax,
                maxPax,
                tourRequest.price(),
                countryRepository.findByIdAndActiveTrue(tourRequest.countryCode())
                        .orElseThrow(()-> new TourException(ErrMsg.COUNTRY_NOT_FOUND)),
                agencyRepository.findByIdAndActiveTrueAndCountryActiveTrue(tourRequest.agencyId())
                        .orElseThrow(()-> new TourException(ErrMsg.AGENCY_NOT_FOUND)),
                createdBy,
                null
        );
        tourRepository.save(tour);
        return TourDetailResponse.fromEntityToDto(tour);
    }

    @Transactional
    public TourDetailResponse update(int id, TourRequest tourRequest, int updatedBy) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new TourException(ErrMsg.TOUR_NOT_FOUND));
        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS))
            throw new TourException(ErrMsg.TOUR_STATUS_NOT_UPDATABLE);
        // Verifica date
        LocalDate startDate = tourRequest.startDate();
        LocalDate endDate = tourRequest.endDate();
        if(startDate.isAfter(endDate))
            throw new TourException(ErrMsg.STARTDATE_ERROR);
        // Verifica numero di partecipanti
        short maxPax = tourRequest.maxPax();
        short minPax = tourRequest.minPax();
        if(minPax > maxPax)
            throw new TourException(ErrMsg.MINPAX_ERROR);

        tour.setName(tourRequest.name());
        tour.setDescription(tourRequest.description());
        tour.setMinPax(minPax);
        tour.setMaxPax(maxPax);
        tour.setStartDate(startDate);
        tour.setEndDate(endDate);
        tour.setPrice(tourRequest.price());
        tour.setCountry(countryRepository.findByIdAndActiveTrue(tourRequest.countryCode())
                .orElseThrow(()-> new TourException(ErrMsg.COUNTRY_NOT_FOUND)));
        tour.setAgency(agencyRepository.findByIdAndActiveTrueAndCountryActiveTrue(tourRequest.agencyId())
                .orElseThrow(()-> new TourException(ErrMsg.AGENCY_NOT_FOUND)));
        tour.setUpdatedBy(updatedBy);

        return TourDetailResponse.fromEntityToDto(tour);
    }

    @Transactional
    public TourDetailResponse publishTour(int id, int updatedBy) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new TourException(ErrMsg.TOUR_NOT_FOUND));
        if(tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS)) {
            tour.setStatus(TourStatus.OPEN);
            tour.setUpdatedBy(updatedBy);
        } else {
            throw new TourException(ErrMsg.TOUR_STATUS_NOT_UPDATABLE);
        }
        return TourDetailResponse.fromEntityToDto(tour);
    }

    public TourDetailResponse getTour(int id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new TourException(ErrMsg.TOUR_NOT_FOUND));
        List<HeroImageResponse> images = heroImageRepository.getImages(id);
        TourDetailResponse tourDetailResponse = TourDetailResponse.fromEntityToDto(tour);
        tourDetailResponse.setHeroImages(images);

        return tourDetailResponse;
    }

    public TourHomePageResponsePaginated toursInHomePage(int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<TourHomePageResponse> page = tourRepository.toursInHomePage(pageable, TourStatus.OPEN);
        List<TourHomePageResponse> list = page.getContent();
        for(TourHomePageResponse t : list){
            t.setDuration(ChronoUnit.DAYS.between(t.getStartDate(), t.getEndDate()));
            List<String> heroImagesIds = heroImageRepository.getHeroImageIds(t.getId());
            // estrazione casuale di una delle immagini associate al tour
            if(!heroImagesIds.isEmpty()) {
                String randomImageId = heroImagesIds.get(ThreadLocalRandom.current().nextInt(heroImagesIds.size()));
                HeroImageResponse heroImageResponse = heroImageRepository.getHeroImageById(randomImageId);
                t.setImage(heroImageResponse);
            } else
                t.setImage(null);
        }
        return new TourHomePageResponsePaginated(pageNumber, pageSize, page.getTotalElements(), page.getTotalPages(), page.getContent());
    }

    public List<TourFindResponse> findTours(String keyword, LocalDate start, LocalDate end) {
        if(keyword == null && start == null && end == null)
            throw new TourException(ErrMsg.NO_PARAMETER_TO_FOUND);
        if(end != null && start == null)
            throw new TourException(ErrMsg.ONLY_END_DATE_NOT_ALLOWED);
        if(start != null && end != null && start.isAfter(end))
            throw new TourException(ErrMsg.STARTDATE_ERROR);

        if(keyword != null)
            keyword = '%'+keyword+'%';
        List<TourFindResponse> list = tourRepository.findTours(keyword, start, end, TourStatus.OPEN);
        return list;
    }

    @Transactional
    public TourInfoForBookingResponse isBookableTour(BookingsTourRequest bookingsTourRequest) {
        Tour tour = tourRepository.findByIdAndStatus(bookingsTourRequest.getTourId(), TourStatus.OPEN)
                .orElse(null);
        if(tour == null)
            return null;
        long maxPax = tour.getMaxPax();
        if(bookingsTourRequest.getBookings() + 1L == maxPax) // se la prenotazione + 1 raggiunge il numero max di persone, cambio stato al tour
            tour.setStatus(TourStatus.SOLD_OUT);
        TourInfoForBookingResponse tourInfoForBookingResponse = new TourInfoForBookingResponse(
                tour.getName(),
                tour.getStartDate(),
                tour.getCountry().getName(),
                tour.getAgency().getName(),
                tour.getPrice(),
                true
        );
        return tourInfoForBookingResponse;
    }

    @Transactional
    public void unbookTour(int tourId) {
        tourRepository.findByIdAndStatus(tourId, TourStatus.SOLD_OUT)
                .ifPresent(tour -> tour.setStatus(TourStatus.OPEN));
    }
}
