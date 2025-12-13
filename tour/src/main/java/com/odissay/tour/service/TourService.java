package com.odissay.tour.service;

import com.odissay.tour.exception.*;
import com.odissay.tour.model.GenericMail;
import com.odissay.tour.model.dto.reponse.TourDetailResponse;
import com.odissay.tour.model.dto.reponse.TourResponse;
import com.odissay.tour.model.dto.reponse.TourResponsePaginated;
import com.odissay.tour.model.dto.request.TourRequest;
import com.odissay.tour.model.entity.*;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.model.entity.emurator.VoucherType;
import com.odissay.tour.repository.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {

    private final TourRepository tourRepository;
    private final CountryRepository countryRepository;
    private final BranchRepository branchRepository;
    private final EmailService emailService;
    private final PayementRepository payementRepository;
    private final VoucherRepository voucherRepository;


    public TourDetailResponse create(TourRequest req){



        String name = req.getName().trim();
        String description = req.getDescription().trim();

        if(req.getEndDate().isBefore(req.getStartDate()))
            throw new Exception400("La data di fine tour non può essere antecedente a quella di inizio.");

        if(req.getMinPax() > req.getMaxPax())
            throw new Exception400("il numero minimo di partecipanti non può essere maggiore del numero massimo degli stessi.");

        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+req.getCountryId()));

        Branch branch = branchRepository.findByIdAndActiveTrue(req.getBranchId())
                .orElseThrow(()-> new Exception404("Filiale non trovata con id "+req.getBranchId()));
         System.out.println( req.getMinPax());
        System.out.println( "ddddddddddddddddd");
        Tour tour = new Tour(
                branch,
                country,
                name,
                description,
                req.getStartDate(),
                req.getEndDate(),
                req.getMinPax(),
                req.getMaxPax(),
                req.getPrice());

        if(tourRepository.existsByName(tour.getName()))

            throw new TourAlreadyExistException("Tour già esistente");

                tourRepository.save(tour);

                return TourDetailResponse.fromEntityToDto(tour);

    }

    public TourResponsePaginated getAllTours(Integer branchId , int pageSize, int pageNumber, String sortBy, String direction) {

       Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.Direction.valueOf(direction.toUpperCase()),sortBy);
       Page<TourResponse> page =tourRepository.getAllTours(branchId,pageable);

        return new TourResponsePaginated (pageNumber, pageSize, page.getTotalElements(), page.getTotalPages(), page.getContent());



        }
    public TourResponsePaginated getFilteredTours(Integer branchId , String status, Short countryId ,LocalDate startDate,LocalDate endDate,Float minPax,Float maxPax ,Double avg,String keyWord,boolean isCaseSensitive,boolean isExactematch, int pageSize, int pageNumber, String sortBy, String direction) {

      /*   Page<TourResponse> page = null;
        List<TourResponse> filteredList = new ArrayList<>();
        try{

        TourStatus tourStatus = status !=null ?TourStatus.valueOf(status.toUpperCase().trim()) : null;
         if(endDate!=null&& startDate==null)
             throw  new Exception400("Non è possibile valorizzare la data di fine periodo");

         if(endDate!=null &&endDate.isBefore(startDate))
             throw  new Exception400("non è possibile avere un endDate precedente a una start date");

         if(maxPax !=null&&maxPax<minPax )
             throw new Exception400("il prezzo massimo è minore del prezzo minimo");
         if(avg != null && avg > 5d)
              throw new Exception400("Il valore della media non può essere maggiore di 5.00");
         Pageable pageable = PageRequest.of(pageNumber,pageSize,Sort.Direction.valueOf(direction.toUpperCase()),sortBy);

         page =tourRepository.getFilteredTours(branchId,tourStatus,countryId,startDate,endDate,minPax,maxPax,avg,keyWord,pageable);
         if(!page.getContent().isEmpty()&&keyWord!=null){


           Pattern  pattern = null;
             if(!isCaseSensitive && !isExactematch)
             pattern = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
                else if(!isCaseSensitive && isExactematch)
                 pattern = Pattern.compile("\\b"+keyWord+"\\b", Pattern.CASE_INSENSITIVE);
             else if(isCaseSensitive && !isExactematch)
                 pattern = Pattern.compile(keyWord);
             else
                 pattern = Pattern.compile("\\b"+keyWord+"\\b");
             for(TourResponse tour : page.getContent()){
                 if(pattern.matcher(tour.getName().concat(" ").concat(tour.getDescription())).find())
                     filteredList.add(tour);
             }
         }
         } catch (IllegalArgumentException e){

             throw new Exception400("Stato del tour + " + status + "non valido" );
         }

        return new TourResponsePaginated (pageNumber, pageSize, page.getTotalElements(), page.getTotalPages(), page.getContent());
    }*/

        List<TourResponse> list = new ArrayList<>();
        Pageable pageable= PageRequest.of(pageNumber, pageSize);

        try {
            // check tour status
            TourStatus tourStatus = status != null ? TourStatus.valueOf(status.toUpperCase().trim()) : null;
            // check date range
            if(endDate != null && startDate == null)
                throw new Exception400("Non è possibile valorizzare solo la data di fine periodo.");
            if(endDate != null && endDate.isBefore(startDate))
                throw new Exception400("La data di fine tour è antecedente a quella di partenza.");
            // check price range
            if(maxPax != null && maxPax < minPax)
                throw new Exception400("il prezzo massimo è minore di quello minimo.");
            // check avg value
            if(avg != null && avg > 5d)
                throw new Exception400("Il valore della media non può essere maggiore di 5.00");
             pageable = PageRequest.of(pageNumber, pageSize);
            Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            list = tourRepository.getFilteredTours(branchId, tourStatus, countryId, startDate, endDate, minPax, maxPax, avg, keyWord != null ? '%'+keyWord+'%' : keyWord, sort);
            // check keyword
            if(!list.isEmpty() && keyWord != null){
                Pattern pattern = null;
                if(!isCaseSensitive && !isExactematch)
                    pattern = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
                else if(!isCaseSensitive && isExactematch)
                    pattern = Pattern.compile("\\b"+keyWord+"\\b", Pattern.CASE_INSENSITIVE);
                else if(isCaseSensitive && !isExactematch)
                    pattern = Pattern.compile(keyWord);
                else
                    pattern = Pattern.compile("\\b"+keyWord+"\\b");
                Pattern finalPattern = pattern;
                list = list.stream()
                        .filter(tour -> finalPattern.matcher(tour.getName().concat(" ").concat(tour.getDescription())).find())
                        .toList();
            }
        } catch (IllegalArgumentException e){
            throw new Exception400("Stato del tour '"+status+"' non valido.");
        }
        Page<TourResponse> page = PaginationService.listToPage(list,pageable);
        return  new TourResponsePaginated(pageNumber, pageSize, page.getTotalElements(), page.getTotalPages(), page.getContent());
    }


    public TourDetailResponse update(int id, TourRequest req) {

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ExpressionException(" tour con id  " + id + "not  presente"));
        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS))
            throw new Exception422("il tour non puo essere modificato");
        String name = req.getName().trim();
        String description = req.getDescription().trim();

        if(req.getEndDate().isBefore(req.getStartDate()))
            throw new Exception400("La data di fine tour non può essere antecedente a quella di inizio.");

        if(req.getMinPax() > req.getMaxPax())
            throw new Exception400("il numero minimo di partecipanti non può essere maggiore del numero massimo degli stessi.");

        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+req.getCountryId()));

        Branch branch = branchRepository.findByIdAndActiveTrue(req.getBranchId())
                .orElseThrow(()-> new Exception404("Filiale non trovata con id "+req.getBranchId()));
        System.out.println( req.getMinPax());
        System.out.println( "ddddddddddddddddd");

        tour.setBranch(branch);
        tour.setCountry(country);
        tour.setName(name);
        tour.setDescription(description);
        tour.setStartDate(req.getStartDate());
        tour.setMaxPax(req.getMaxPax());
        tour.setMinPax(req.getMinPax());
        tour.setPrice(req.getPrice());



        /*Tour tour = new Tour(
                branch,
                country,
                name,
                description,
                req.getStartDate(),
                req.getEndDate(),
                req.getMinPax(),
                req.getMaxPax(),
                req.getPrice());

        if(tourRepository.existsByName(tour.getName()))

            throw new TourAlreadyExistException("Tour già esistente");
   */

        try {
        tourRepository.save(tour);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception422("lo stesso tour per la stessa data è già stato creato");
        }
        return TourDetailResponse.fromEntityToDto(tour);


    }
    @Transactional
    public TourDetailResponse cancelTour(int id) {

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() ->new Exception404("il tour con id  " + id + " not trovato"));
        try{
        if(tour.getStatus().equals(TourStatus.EXPIRED))
            throw new Exception400("il tour è già stato concluso");
        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS) || (!tour.getStatus().equals(TourStatus.CANCELED))){
              // verificare che ci siano i partecipanti e avisarli

            Set<Customer> customers = tour.getCustomers();
                 for(Customer customer: customers){

                     try{
                         emailService.sendMail(sendNotification(customer.getUser(),tour.getName()));
                        // rimborso
                        // 1 verificare chi dei customer a già versato la propria quota
                           Float amountToRefound = payementRepository.sumPaymentByTourAndCustomer(customer.getId(),tour.getId());
                           //2.se ha effe

                           if(amountToRefound !=null){
                               Vaucher voucher = new Vaucher(customer,amountToRefound, VoucherType.REFUND,tour.getBranch().getName());
                               voucherRepository.save(voucher);
                           }


                     } catch (MessagingException ex){
                         log.error("Email non inviato a " + customer.getUser());

                     }
                 }
        }

        tour.setStatus(TourStatus.CANCELED);
        tourRepository.save(tour);

        return  TourDetailResponse.fromEntityToDto(tour);

        } catch (Exception e) {
            // QUI CATTURI TUTTO
            System.out.println("Eccezione catturata: " + e.getMessage());
            throw e;

        }
    }

    public TourDetailResponse changeTourStatus(int id,String status) {


        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+id));
        Country country = countryRepository.findByIdAndActiveTrue(tour.getCountry().getId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+id));

        try { TourStatus newStatus = TourStatus.valueOf(status);

        TourStatus oldStatus =tour.getStatus()
                ;
           if(newStatus.equals(TourStatus.WORK_IN_PROGRESS)&&!oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
              throw new Exception400("il tour è già in vendita lo stato work in Progresse non è utillizabile");
           if(newStatus.equals(TourStatus.WORK_IN_PROGRESS)&& oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
            throw new Exception400("il tour è già in vendita lo stato work in Progresse");
        //OPEN
            System.out.println(oldStatus);
            System.out.println(newStatus);
            if(newStatus.equals(TourStatus.OPEN) && !oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
                throw new Exception400("Il tour è già vendita.");
            //if(newStatus.equals(TourStatus.OPEN) && oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
             //   throw new Exception400("Il tour è già in status OPEN."); da chiedere al prof

            // SOLD_OUT
            if(newStatus.equals(TourStatus.SOLD_OUT))
                throw new Exception400("Il tour può cambiare stato in SOLD_OUT solo in base alle prenotazioni."); // Cambio eseguito da schedulazione

            // IN_PROGRESS
            if(newStatus.equals(TourStatus.IN_PROGRESS))
                throw new Exception400("Il tour può cambiare stato in IN_PROGRESS solo in base alla data di partenza."); // Cambio eseguito da schedulazione

            // EXPIRED
            if(newStatus.equals(TourStatus.EXPIRED))
                throw new Exception400("Il tour può cambiare stato in EXPIRED solo se la data attuale è successiva alla data di fine tour."); // Cambio eseguito da schedulazione

            // NOT_SOLD_OUT
            if(newStatus.equals(TourStatus.NOT_SOLD_OUT))
                throw new Exception400("Il tour può cambiare stato in NOT_SOLD_OUT solo se la data attuale coincide con la data di inizio tour e non si è raggiunto il numero minimo di partecipanti."); // Cambio eseguito da schedulazione

            // CANCELED
            if(newStatus.equals(TourStatus.CANCELED))
                throw new Exception401("Solo l'amministratore può cancellare il tour d'ufficio");


            tour.setStatus(newStatus);
            tour.setCountry(country);
            tourRepository.save(tour);
            return TourDetailResponse.fromEntityToDto(tour);

        } catch (IllegalArgumentException e){
            throw new Exception404("Il nuovo status non è tra quelli validi.");
        }
    }


    @Transactional
    public String earlyCancellation(int tourId, int customerId){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(()-> new Exception404("Tour on trovato con id "+tourId));
        if(
                (!tour.getStatus().equals(TourStatus.OPEN) && !tour.getStatus().equals(TourStatus.SOLD_OUT)) &&
                        tour.getStartDate().isAfter(LocalDate.now().plusDays(5L))
        ) throw new Exception409("Non puoi disdire il tour.");

        Set<Customer> customers = tour.getCustomers();
        if(customers.stream().noneMatch(c -> c.getId() == customerId))
            throw new Exception400("Non puoi disdire un tour che non hai prenotato");

        Customer customer = new Customer(customerId);
        tour.getCustomers().remove(customer);

        tour.setStatus(TourStatus.OPEN);

        Float amountToRefund = payementRepository.sumPaymentByTourAndCustomer(tour.getId(), customerId);
        if(amountToRefund != null){
            Vaucher voucher  = new Vaucher(customer, amountToRefund*0.6f, VoucherType.REFUND, tour.getBranch().getName());
            voucherRepository.save(voucher);
        }
        return "Disdetta del tour "+tour.getName()+" da parte del customer avente id "+customerId+" avvenuta con successo";
    }

    public static GenericMail sendNotification(User user, String tourName){
        GenericMail mail = new GenericMail();
        mail.setTo(user.getEmail());
        mail.setSubject("Tour Odissey: cancellazione tour '"+tourName+"'");
        mail.setBody("Gentile "+user.getFirstname()+" "+user.getLastname()+",\nci dispiace informarLa che il tour in oggetto è stato cancellato per motivi non dipendenti dalla nostra volontà.\nSeguiranno altre comunicazioni per il rimborso.");
        return mail;
    }


}
