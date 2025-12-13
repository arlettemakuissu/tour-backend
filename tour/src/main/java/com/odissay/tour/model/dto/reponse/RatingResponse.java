package com.odissay.tour.model.dto.reponse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    private int tourId;
    private int rate;
    private double avg;





}
