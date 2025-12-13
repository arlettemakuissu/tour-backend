package com.odissay.tour.service;

import com.odissay.tour.exception.Exception500;
import com.odissay.tour.model.GenericMail;
import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Tour;
import com.odissay.tour.model.entity.Vaucher;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.model.entity.emurator.VoucherType;
import com.odissay.tour.repository.PayementRepository;
import com.odissay.tour.repository.TourRepository;
import com.odissay.tour.repository.VoucherRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulingService {

     private  final TourRepository tourRepository;
     private final PayementRepository payementRepository;
     private final VoucherRepository voucherRepository;
     private final EmailService emailService;

    //I METODI SOTTOPOSTI A SCHEDULAZIONE DEVONO SEMPRE ESSERE VOID

    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html#parse(java.lang.String)
    //@Scheduled(fixedRate = 10000000) // tempo espresso in millisecondi
    //@Scheduled(cron = "*/10 * * * * *") // ogni 10 secondi
    //@Scheduled(cron = "@daily") // ogni giorno a mezzanotte
    @Scheduled(cron = "0 0 9-12 * * *") // ogni giorno alle 9, 10, 11 e 12
    @Transactional
    public void changeTourStatus(){

        log.info(">> Sto cercando i tour a cui cambiare stato");

         Set<TourStatus> statuses = new  HashSet<>();
         statuses.add(TourStatus.CANCELED);
         statuses.add(TourStatus.EXPIRED);
         statuses.add(TourStatus.IN_PROGRESS);

        List<Tour> validTours = tourRepository.findValidTours(statuses);

        List <Tour> tours = new ArrayList<>();
        for(Tour t :validTours  ){
            LocalDate now = LocalDate.now();
          TourStatus status = t.getStatus();
            if( (status.equals(TourStatus.OPEN) || status.equals(TourStatus.SOLD_OUT)) && t.getStartDate() == now) {
                if (t.getCustomers().size() >= t.getMinPax() )
                    t.setStatus(TourStatus.IN_PROGRESS);
                else
                    t.setStatus(TourStatus.NOT_SOLD_OUT);
                tours.add(t);
            }
            if( status.equals(TourStatus.IN_PROGRESS) && t.getEndDate().plusDays(1L) == now) {
                t.setStatus(TourStatus.EXPIRED);
                tours.add(t);
            }
        }
        tourRepository.saveAll(tours);

    }

    private void refund(Tour tour, Set<Customer> customers){
        List<Vaucher> vouchers = new ArrayList<>();
        for(Customer c : customers){
            try {
                emailService.sendMail(sendNotificationForNotSoldOut(c, tour));
            } catch (MessagingException e){
                throw new Exception500("Si è verificato un errore duranto l'invio dell'email");
            }
            Float amountToRefund = payementRepository.sumPaymentByTourAndCustomer(tour.getId(), c.getId());
            if(amountToRefund != null) {
                Vaucher voucher = new Vaucher(c, amountToRefund, VoucherType.REFUND, tour.getBranch().getName());
                vouchers.add(voucher);
            }
        }
        voucherRepository.saveAll(vouchers);
    }

    public static GenericMail sendNotificationForNotSoldOut(Customer customer, Tour tour ){
        GenericMail mail = new GenericMail();
        mail.setTo(customer.getUser().getEmail());
        mail.setSubject("Tour Odissey: cancellazione " +tour.getName()+ "");
        mail.setBody("Gentile " + customer.getUser().getFirstname() + " " + customer.getUser().getLastname()+",\nci dispiace informarLa che il tour in oggetto è stato cancellato in quanto non è stato raggiunto il numero minimo di partecipanti.\nLe è stato assegnato un voucher di rimborso (qualora abbia effettuato dei pagamenti per il tour in oggetto) spendibile presso " +tour.getBranch().getName());
        return mail;
    }

}
