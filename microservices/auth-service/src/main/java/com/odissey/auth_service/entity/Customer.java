package com.odissey.auth_service.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class
Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User userId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private boolean receiveNewsletter;

    @Column(nullable = false)
    private boolean acceptServiceTerms;

    public Customer(User userId, String address, String city, boolean receiveNewsletter, boolean acceptServiceTerms) {
        this.userId = userId;
        this.address = address;
        this.city = city;
        this.receiveNewsletter = receiveNewsletter;
        this.acceptServiceTerms = acceptServiceTerms;
    }
}
