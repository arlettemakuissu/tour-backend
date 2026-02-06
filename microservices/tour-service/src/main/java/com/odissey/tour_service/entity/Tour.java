package com.odissey.tour_service.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 15360) // 15Kb dai 16Kb in su è richiesto un campo almeno TEXT
    //@Column(nullable = false, columnDefinition="TEXT") // 64Kb
    private String description; // piano di viaggio

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<HeroImage> heroImages = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private short minPax;
    private short maxPax;

    private float price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Agency agency;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(updatable = false)
    private int createdBy;

    private Integer updatedBy;

    private boolean inHomePage;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public Tour(String name, String description, LocalDate startDate, LocalDate endDate, short minPax, short maxPax, float price, Country country, Agency agency, int createdBy, Integer updatedBy) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minPax = minPax;
        this.maxPax = maxPax;
        this.price = price;
        this.country = country;
        this.agency = agency;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.status = TourStatus.WORK_IN_PROGRESS;
        this.inHomePage = false;
    }

    public void addHeroImage(HeroImage heroImage){
        heroImages.add(heroImage);
        heroImage.setTour(this);
    }
}
