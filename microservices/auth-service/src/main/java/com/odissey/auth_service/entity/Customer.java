package com.odissey.auth_service.entity;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @OneToOne
    @JoinColumn(nullable = false,name = "user_id" )
    private User  userId;
    @JoinColumn(nullable = false)
    private String address;

    @JoinColumn(nullable = false)
    private String city;
    @JoinColumn(nullable = false)
    private boolean receiveNewsletter;
    @JoinColumn(nullable = false)
    private boolean acceptServiceTerms;


    public Customer( User userId,String address,  String city,boolean receiveNewsletter, boolean acceptServiceTerms) {
        this.userId = userId;
        this.address = address;
        this.city = city;
        this.receiveNewsletter = receiveNewsletter;
        this.acceptServiceTerms = acceptServiceTerms;
    }
}
