package com.odissay.tour.model.dto.swagger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerMap {
    private Map<String,String> error;
}
