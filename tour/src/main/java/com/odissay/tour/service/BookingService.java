package com.odissay.tour.service;

import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Tour;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.model.entity.User;
import com.odissay.tour.repository.CustomerRepository;
import com.odissay.tour.repository.TourRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public  String bookingTour( int tourId, UserDetails userDetails) {

        System.out.println("Class: " + userDetails.getClass());
        System.out.print("yesssssssssssss");




       try{
           Tour tour = tourRepository.findById(tourId)
                   .orElseThrow(  () -> new  Exception400("il tour non tovato con id"));
    if(!tour.getStatus().equals(TourStatus.OPEN))
        throw  new Exception400("Il tour si è già concluso oppure è soldOut ");
           System.out.print("uuuuuuuuuuuu");
           System.out.println("Principal class = " + userDetails.getUsername());
           System.out.println("userDetails instanceof User = " + (userDetails instanceof User));
           System.out.println("REAL CLASS = " + userDetails.getClass());
           Customer customer = null;

               User user = (User) userDetails;
               // Ricarico il Customer dalla sessione
               customer = customerRepository.findById(user.getCustomer().getId())
                       .orElseThrow(() -> new Exception400("Customer non trovato"));



           System.out.print("iiiiiiiiiiii");
           System.out.println("Prima di getCustomers");
           System.out.print(customer.getId());
           int currentCustomers = tour.getCustomers().size();
           System.out.println("Numero clienti attuali: " + currentCustomers);
           // verificare che il tour che il customer vuole prenotare non si sovrapponga con
           // eventuali altri tour che ha prenotato neanche per un giorno
           // e che non stia prenotando un viaggio che ha già prenotato



           Set<Tour> customerTours = tourRepository.findBookedToursByCustomer(customer.getId());
           for(Tour t : customerTours) {
               if(t.getStartDate().isBefore(tour.getEndDate()) &&
                       t.getEndDate().isAfter(tour.getStartDate()) &&
                       t.getStatus().equals(TourStatus.OPEN) &&
                       !t.getId().equals(tour.getId())
               )
                   throw new Exception409("Il tour che vuoi prenotare si sovrappone ad un altro tour che hai gia prenota: "+t.getName()+" ("+t.getStartDate()+" - "+t.getEndDate()+")");
               if(t.getId().equals(tour.getId()))
                   throw new Exception409("Hai già prenotato questo viaggio");
           }

           if(tour.getMaxPax()>tour.getCustomers().size())
         tour.addCustomer(customer);
      if(tour.getMaxPax()==tour.getCustomers().size())
          tour.setStatus(TourStatus.SOLD_OUT);
      tourRepository.save(tour);

      return "Complimenti "+customer.getUser().getFirstname()+" "+customer.getUser().getLastname()+", hai prenotato il tour "+tour.getName();
       } catch (Exception e){

           System.out.println("messagio di errore  " + e.getMessage());
         throw e;
       }

    }


}
