package com.emanueldev.sample_shop.domain.dtos.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private Long stockQuantity;
}
