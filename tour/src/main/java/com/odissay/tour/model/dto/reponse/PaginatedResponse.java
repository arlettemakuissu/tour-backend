package com.odissay.tour.model.dto.reponse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaginatedResponse<T> {

  private  int page;
  private  int size;
  private long totalItems;
  private long totalPages;


}
