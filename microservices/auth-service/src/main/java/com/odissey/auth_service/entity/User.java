package com.odissey.auth_service.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;

@Entity
@Table(name ="Users" ,uniqueConstraints = {@UniqueConstraint(columnNames ="userName", name = "email")})

@Getter
@Setter
@NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

     @Column(nullable = false,length = 20)
     private String username;

    @Column(nullable = false,length = 20)
     private String email;
    @Column(nullable = false)
    private String passwordHash;
    private String roles;//OPERATOR,CUSTOMER
    private boolean enable;


}
