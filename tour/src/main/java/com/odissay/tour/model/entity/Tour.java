package com.odissay.tour.model.entity;

import com.odissay.tour.model.entity.emurator.TourStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tours", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "branch_id", "start_date"})})
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Tour extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Country country;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 15360) // 20Kb
    private String description; // piano di viaggio



    private String image; // path to image <img src="/pic/image_09.jpg">

    @Column(nullable = false)
    private LocalDate startDate;


    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status;



    private int minPax;
    private int maxPax;

    private float price;

    private double avgRating;
    @ManyToMany  @JoinTable(name = "bookings",
            joinColumns = @JoinColumn(name = "tour_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"))
    private Set<Customer> customers = new HashSet<>();


    public Tour(Branch branch, Country country, String name, String description, LocalDate startDate, LocalDate endDate, int minPax, int maxPax, float price) {
        this.branch = branch;
        this.country = country;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minPax = minPax;
        this.maxPax = maxPax;
        this.price = price;
        status = TourStatus.WORK_IN_PROGRESS;
    }

    // SINCRONIZZAZIONE
    public void addCustomer(Customer customer) {
     customers.add(customer);
     customer.getTours().add(this);
     }
     public void removeCustomer(Customer customer) {
     customers.remove(customer); customer.getTours().remove(this);
     }
}
