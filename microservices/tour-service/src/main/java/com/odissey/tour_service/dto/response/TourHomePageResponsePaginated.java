package com.odissey.tour_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class TourHomePageResponsePaginated extends PaginatedResponse{

    private List<TourHomePageResponse> data;

    public TourHomePageResponsePaginated(int page, int size, long totalItems, long totalPages, List<TourHomePageResponse> data) {
        super(page, size, totalItems, totalPages);
        this.data = data;
    }
}
