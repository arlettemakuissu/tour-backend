package com.odissey.tour.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String password; // Password123!

    private LocalDateTime lastChangePassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(length = 6)
    private String otpCode;


    public User(String username, String email, String firstname, String lastname, String password, Role role) {
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.role = role;
        this.verified = false;
        this.enabled = false;
    }

}
