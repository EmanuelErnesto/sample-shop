package com.emanueldev.sample_shop.domain.payments.dtos.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class PaginatedPaymentResponseDTO {

    List<PaymentResponseDTO> payments;
    int current_page;
    long total_items;
    int total_pages;
}
