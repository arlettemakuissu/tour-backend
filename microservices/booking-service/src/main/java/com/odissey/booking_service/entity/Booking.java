package com.odissey.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_id", "tour_id", "code"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int customerId;

    @Column(nullable = false)
    private int tourId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

    private boolean canceled; // if true -> rinuncia alla prenotazione

    @Column(nullable = false, length = 36)
    private String code; // uuid -> codice prenotazione

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public Booking(int customerId, int tourId, String code) {
        this.customerId = customerId;
        this.tourId = tourId;
        this.code = code;
        this.canceled = false;
    }
}
