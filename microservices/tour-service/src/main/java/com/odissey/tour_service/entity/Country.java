package com.odissey.tour_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

}
