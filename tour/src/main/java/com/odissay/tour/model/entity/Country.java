package com.odissay.tour.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name="countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) @ToString
public class Country extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private short id;
    @Column(length=2,nullable = false,unique=true)
    private String code;
    @Column (nullable=false,unique=true)
    private String name;
    @Column(nullable = false,unique=true)
    private String currency;
    @Column(name = "is_active",nullable = false)
    private boolean active;
     public Country (String code,String name,String currency
     ){

         this.code =code;
         this.name = name;
         this.currency = currency;
         this.active = true;
     }
}
