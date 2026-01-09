package com.odissey.country_service.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "countries")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Country {

    @Id
    @EqualsAndHashCode.Include
    @Column(length = 2)
    private String id; // ISO-2 code (es. IT, FR, DE, etc...)

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String currency;

    @Column(name = "is_active", nullable = false)
    private boolean active;

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

    public Country(String id, String name, String currency, int createdBy, Integer updatedBy) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.active = true;
    }

}
