package com.odissay.tour.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass// dire a country di considare i dati come la sua entità e quindi della tabella countries
public class AuditableEntity {

    @Column(updatable = false,nullable = false)
    private LocalDateTime createAt;


    private LocalDateTime updateAt;

    @PrePersist// quindi prima di persistere il valore su db valorizza creatat
    protected void onCreate(){
        createAt = LocalDateTime.now();
    }

    @PreUpdate // prima che deve aggiornato
    protected  void onUpdate(){
        updateAt = LocalDateTime.now();
    }

}
