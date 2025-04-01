package com.emanueldev.sample_shop.domain.orders.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedOrderResponseDTO {

    List<OrderResponseDTO> orders;
    int current_page;
    long total_items;
    int total_pages;
}
