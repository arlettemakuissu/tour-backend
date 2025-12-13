package com.odissay.tour.model.dto.reponse;

import com.odissay.tour.model.entity.Tour;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class TourResponsePaginated extends PaginatedResponse{



    private List<TourResponse> data;

    public TourResponsePaginated(int page, int size, long totalItems, long totalPages,  List<TourResponse> data) {
        super(page, size, totalItems, totalPages);
        this.data = data;
    }
}
