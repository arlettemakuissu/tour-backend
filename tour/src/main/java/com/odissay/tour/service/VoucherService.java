package com.odissay.tour.service;

import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.model.dto.reponse.VoucherReceiptResponse;
import com.odissay.tour.model.dto.request.VoucherRequest;
import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Vaucher;
import com.odissay.tour.model.entity.emurator.VoucherType;
import com.odissay.tour.repository.CustomerRepository;
import com.odissay.tour.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherService {

private final CustomerRepository customerRepository;
private final VoucherRepository voucherRepository;


    public VoucherReceiptResponse create(VoucherRequest req){


            String type = req.getType().trim().toUpperCase();
            if(!type.equals(VoucherType.GIFT.name())&&!type.equals(VoucherType.REFUND.name()))
               throw new Exception400("la tipologia del voucher non è valida");
            System.out.println(req.getCustomerId());
            Customer customer = customerRepository.findById(req.getCustomerId())
                    .orElseThrow(()-> new Exception404("Customer non trovato con id "+req.getCustomerId()));


        System.out.println(req.getCustomerId());
        Vaucher voucher = new Vaucher(customer,req.getPrice(),VoucherType.valueOf(type),req.getEmittedBy());

        System.out.println("aaaaaaaaaaaaaaa");
        System.out.println(voucher.getEmittedBy());

        voucherRepository.save(voucher);
        return VoucherReceiptResponse.fromEntityToDto(voucher);


    }
}
