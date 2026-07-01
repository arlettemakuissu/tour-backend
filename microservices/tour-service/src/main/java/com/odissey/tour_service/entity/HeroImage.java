package com.odissey.tour_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HeroImage {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String mimeType; // image/jpg

    @Lob // di default genera un TINYBLOB; con columnDefinition="BLOB" forzo la tipologia. Il nome dei tipi di campi sul db dipende dal vendor
    @Column(nullable = false , columnDefinition = "BLOB", length = 61440 /* 60Kb */) // TINYBLOB: 255 bytes; BLOB:	64 Kb;  MEDIUMBLOB: 16 MB; LONGBLOB	4 GB -> valgono solo per MySQL
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Tour tour;

    @Column(nullable = false)
    private int prg; // progressivo che indica l'ordine di visualizzazione delle immagini

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(updatable = false)
    private int createdBy;

    private Integer updatedBy;

    @Column(nullable = false, updatable = false)
    private String checkSumAlgorithm;
    @Column(nullable = false, updatable = false)
    private String checksum;

    @Column(nullable = false)
    private long size; // peso del file espresso in byte

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public HeroImage(String id, String filename, String mimeType, byte[] data, Tour tour, int prg, int createdBy, Integer updatedBy, String checkSumAlgorithm, String checksum, long size) {
        this.id = id;
        this.filename = filename;
        this.mimeType = mimeType;
        this.data = data;
        this.tour = tour;
        this.prg = prg;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.checkSumAlgorithm = checkSumAlgorithm;
        this.checksum = checksum;
        this.size = size;
    }
}
