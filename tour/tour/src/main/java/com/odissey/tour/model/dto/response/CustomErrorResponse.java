package com.odissey.tour.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor @NoArgsConstructor
public class CustomErrorResponse {

    private String type;
    private String title;
    private int status;
    // private String detail; // Commentata in quanto non è auspicabile che finisca nella resposne
    private String instance;
    private Map<String, String> errors;
    private LocalDateTime when = LocalDateTime.now();
}
