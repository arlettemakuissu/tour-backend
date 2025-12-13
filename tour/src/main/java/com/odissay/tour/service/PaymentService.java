package com.odissay.tour.service;

import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.dto.request.PayementRequest;
import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Payment;
import com.odissay.tour.model.entity.Tour;
import com.odissay.tour.model.entity.Vaucher;
import com.odissay.tour.model.entity.emurator.PayementType;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PayementRepository payementRepository;
    private final TourRepository tourRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

   @Transactional
    public String create( PayementRequest req) {




        Tour tour = tourRepository.findById(req.getTourId())
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+req.getTourId()));
        if(!tour.getStatus().equals(TourStatus.OPEN))
            throw new Exception409("Il tour non è prenotabile e quindi non è pagabile");
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(()-> new Exception404("Customer non trovato con id "+req.getCustomerId()));

        // boolean isBooked = verificare che il tour sia stato effettivamente prenotato dal customer,
        // ovvero che esista un record sulla tabella Booking

      // boolean isBooked  = (tour.getCustomers().contains(customer)&&customer.getTours().contains(tour));
              // if(!isBooked)
            //throw  new Exception404("il tour "  + tour + "che stai per pagare non è stato prenotato dal customer " + customer  );


            boolean isBooked = bookingRepository.isCustomerBookedTour(tour.getId(), customer.getId());

            if (!isBooked) {
                throw new Exception404("il tour  " + tour.getId() + " che sta per pagare non è prenotato dal customer " + customer.getId());
            }
        PayementType type = null;
        try{
            type = PayementType.valueOf(req.getPaymentType().trim().toUpperCase());
        } catch (IllegalArgumentException e){
            throw new Exception400("Il tipo di pagamento non è tra quelli ammessi");
        }
        Vaucher voucher = null;

        if(req.getVoucherId() != null && !type.equals(PayementType.VAUCHER))
            throw new Exception409("Se selezioni un voucher da usare il tipo di pagamento deve essere 'voucher'");

        if(req.getVoucherId() != null && type.equals(PayementType.VAUCHER)) {
            voucher = voucherRepository.findByIdAndCustomerIsAndUsedFalse(req.getVoucherId(), customer)
                    .orElseThrow(() -> new Exception404("Voucher non trovato con id " + req.getVoucherId()));
        }
        if(voucher != null && !voucher.getEndValidity().isAfter(LocalDate.now()))
            throw new Exception409("Il voucher è scaduto!");

        if(req.getVoucherId() == null && type.equals(PayementType.VAUCHER))
            throw new Exception400("Il tipo di pagamento prevede l'inserimento di un voucher valido");
        float amount = 0f;
        if(req.getAmount() == null && type.equals(PayementType.VAUCHER))
            amount = voucher.getPrice();

        else if(req.getAmount() == null)
            throw new Exception400("L'importo del pagamento non può essere nullo");
        else if(req.getAmount() != null && type.equals(PayementType.VAUCHER))
            throw new Exception400("Se usi un voucher non devi inserire l'importo.");
        else
            amount = req.getAmount();

        // verificare che il pagamento che stiamo effettuando (sommandosi agli altri già effettuati dal customer) non superi il valore del tour
        Float totalAmount = payementRepository.sumPaymentByTourAndCustomer(tour.getId(), customer.getId());
        totalAmount = totalAmount == null ? 0f : totalAmount;
        if((amount + totalAmount) > tour.getPrice())
            throw new Exception409("Il versamento che vuoi effettuare supera il valore del tour di "+String.format("%.2f", (amount + totalAmount)-tour.getPrice()));

        Payment payment = new Payment(tour, customer, voucher,type, amount);
        payementRepository.save(payment);
        if(voucher != null)
            voucher.setUsed(true);

        String currency = tour.getBranch().getAgency().getCountry().getCurrency();

        return (tour.getPrice()-(amount + totalAmount)) > 0 ?
                "Con questo versamento di "+amount+" "+currency+" ti mancano "+(tour.getPrice()-(amount + totalAmount))+" "+currency+" per saldare il prezzo del viaggio." :
                "Con questo versamento di "+amount+" "+currency+" hai saldato il viaggio!";
        }

}

