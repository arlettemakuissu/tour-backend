package com.odissay.tour.model.entity;

import com.odissay.tour.model.entity.emurator.PayementType;
import com.odissay.tour.model.entity.emurator.PayementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Payment extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tour tour;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Vaucher vaucher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayementType type;
    @Column (nullable = false,precision=2)
    private Float amount;

    public Payment(Tour tour, Customer customer, Vaucher vaucher, PayementType type, Float amount) {
        this.tour = tour;
        this.customer = customer;
        this.vaucher = vaucher;
        this.type = type;
        this.amount = amount;
    }
}
