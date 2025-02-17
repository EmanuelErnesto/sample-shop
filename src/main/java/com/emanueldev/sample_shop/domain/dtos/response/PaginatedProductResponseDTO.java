package com.emanueldev.sample_shop.domain.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class PaginatedProductResponseDTO {

    List<ProductResponseDTO> products;
    int current_page;
    long total_items;
    int total_pages;
}
