package com.emanueldev.sample_shop.domain.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductResponseDTO {

    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private Long stockQuantity;
}
