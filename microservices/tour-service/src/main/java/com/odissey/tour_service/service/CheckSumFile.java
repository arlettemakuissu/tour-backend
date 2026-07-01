package com.odissey.tour_service.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CheckSumFile {
    private String checkSumAlgorithm;
    private String checksum;
}
