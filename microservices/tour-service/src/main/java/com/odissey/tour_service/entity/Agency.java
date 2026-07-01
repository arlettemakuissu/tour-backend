package com.odissey.tour_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agencies")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class Agency {

    @Id
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String vat;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Country country;

    public Agency(String name, String vat, String address, String city, Country country) {
        this.name = name;
        this.vat = vat;
        this.address = address;
        this.city = city;
        this.country = country;
        this.active = true;
    }
}
