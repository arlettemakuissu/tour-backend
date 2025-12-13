package com.odissay.tour.service;


import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.dto.reponse.RatingResponse;
import com.odissay.tour.model.entity.*;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.repository.CustomerRepository;
import com.odissay.tour.repository.RatingRepository;
import com.odissay.tour.repository.TourRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

@Transactional
public class RatingService {

    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final RatingRepository ratingRepository;


    public RatingResponse create(int tourId, int rate, UserDetails userDetails){


        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() ->new Exception404("tour non trovato con id "+ tourId));

        if(!tour.getStatus().equals(TourStatus.EXPIRED))
            throw new Exception400("solo un tour terminato puo essere votato ");

        User user = (User) userDetails;

        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(() -> new Exception409("customer non trovato con l'id " + user.getCustomer().getId()));

        if(!tour.getCustomers().contains(customer))
            throw new Exception400("solo chi ha partecipato al tour puo votare");

        Rating rating = new Rating(new RatingId(customer,tour),rate);

        ratingRepository.save(rating);
        double avg = ratingRepository.calcAvgByTour(tourId);
        tour.setAvgRating(avg);

        return new RatingResponse(tourId,rate,avg);

    }





}
