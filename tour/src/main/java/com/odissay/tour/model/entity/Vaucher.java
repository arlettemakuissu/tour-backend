package com.odissay.tour.model.entity;


import com.odissay.tour.model.entity.emurator.VoucherType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="voucher")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Vaucher extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String code;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Customer customer;
    @JoinColumn(nullable = false)
    private float price;

    @JoinColumn(nullable = false)
    private LocalDate endValidity; // 1 anno a far data dell'aquisito
    @Column(name = "is_used",nullable = false)
    boolean used;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType type;

    @Column(nullable = false)
    private String emittedBy;

    public Vaucher(Customer customer, float price, VoucherType type,  String emittedBy) {
        this.customer = customer;
        this.price = price;
        this.type = type;
        this.emittedBy = emittedBy;
        this.endValidity = LocalDate.now().plusYears(1L);
        this.used = false;
        this.code = UUID.randomUUID().toString();

    }
}
