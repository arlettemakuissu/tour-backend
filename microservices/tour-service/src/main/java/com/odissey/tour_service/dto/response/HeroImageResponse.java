package com.odissey.tour_service.dto.response;

import com.odissey.tour_service.entity.HeroImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class HeroImageResponse {

    private String id;
    private String filename;
    private String mimeType;
    private byte[] data;
    private int prg;

    public static HeroImageResponse fromEntityToDto(HeroImage heroImage){
        return new HeroImageResponse(
                heroImage.getId(),
                heroImage.getFilename(),
                heroImage.getMimeType(),
                heroImage.getData(),
                heroImage.getPrg()
        );
    }
}
