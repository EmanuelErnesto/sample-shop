package com.emanueldev.sample_shop.domain.products.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedProductResponseDTO {

    List<ProductResponseDTO> products;
    int current_page;
    long total_items;
    int total_pages;
}
