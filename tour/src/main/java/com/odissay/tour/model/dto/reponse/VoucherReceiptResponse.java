package com.odissay.tour.model.dto.reponse;

import com.odissay.tour.model.entity.Vaucher;
import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class VoucherReceiptResponse {
    private String emittedBy;
    private String code;
    private int customerId;
    private String fullName;
    private float price;




    public static VoucherReceiptResponse fromEntityToDto(Vaucher voucher){
        return new VoucherReceiptResponse(
                voucher.getEmittedBy(),

                voucher.getCode(),
                voucher.getCustomer().getId(),
                voucher.getCustomer().getUser().getFirstname().concat(" ").concat( voucher.getCustomer().getUser().getLastname()),
                voucher.getPrice()
        );
    }




}
