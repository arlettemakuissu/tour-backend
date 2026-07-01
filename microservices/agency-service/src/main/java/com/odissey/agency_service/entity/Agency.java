package com.odissey.agency_service.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "agencies")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(updatable = false)
    private int createdBy;

    private Integer updatedBy;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public Agency(String name, String vat, String address, String city, Country country, int createdBy, Integer updatedBy) {
        this.name = name;
        this.vat = vat;
        this.address = address;
        this.city = city;
        this.country = country;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.active = true;
    }
}
