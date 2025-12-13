package com.odissay.tour.model.entity;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Rating extends AuditableEntity{


    @EmbeddedId
    @EqualsAndHashCode.Include
    private RatingId ratingId;

    private int rate; // i voti ammessi vanno da 1 a 5





}
