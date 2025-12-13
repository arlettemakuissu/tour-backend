package com.odissay.tour.service;


import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception401;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.model.dto.reponse.CommentResponse;
import com.odissay.tour.model.dto.request.CommentRequest;
import com.odissay.tour.model.entity.Comment;
import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Tour;
import com.odissay.tour.model.entity.User;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.repository.CommentRepository;
import com.odissay.tour.repository.CustomerRepository;
import com.odissay.tour.repository.TourRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CommentResponse create( CommentRequest req, UserDetails userDetails) {
   Tour tour = tourRepository.findById(req.getTourId())
           .orElseThrow(() -> new Exception404("Tour non trovato con id " + req.getTourId()));
   if(!tour.getStatus().equals(TourStatus.EXPIRED))
       throw new Exception400("non si puo commentare un tour che non open");

   User user = (User)userDetails;

   Customer customer = customerRepository.findById(user.getCustomer().getId())
           .orElseThrow(() ->new Exception400("customer non trovato con id " + user.getCustomer().getId()));

   if(!tour.getCustomers().contains(customer))
       throw new Exception401("solo i clienti che hanno partecipato al tour possono commentare");

   Comment refererTo = null;

   if(req.getRefererTo()!=null)
       refererTo = commentRepository.findById(req.getRefererTo())
               .orElseThrow(() ->new Exception400("commento di referimento non trovato"));
   Comment comment = new Comment(customer, tour, req.getContent(), refererTo);

    commentRepository.save(comment);

    return new  CommentResponse(
            comment.getId(),
            user.getFirstname().concat(" ").concat(user.getLastname()),
            comment.getCreateAt(),
            comment.getContent(),
            refererTo == null ? null : refererTo.getId()
    );
    }

    public List<CommentResponse> getCommentsByTour( int tourId) {
       if(tourRepository.existsById(tourId))
           return commentRepository.getCommentsByTour(tourId);

       return new ArrayList<CommentResponse>();


    }

    public String censorComment( int commentId) {

     Comment comment = commentRepository.findById(commentId)
             .orElseThrow(()-> new Exception404("commento non trovato con id "+ commentId) );


    comment.setCensored(true);
     commentRepository.save(comment);
     return "commento" + comment.getContent()+ " censurato.";
    }
}
