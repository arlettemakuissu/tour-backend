package com.odissay.tour.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingId {

    @ManyToOne
    @JoinColumn(nullable = false) // impostando id cosi significa un contomer puo dare solo un comento a un determinao tour.
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tour tour;

}
