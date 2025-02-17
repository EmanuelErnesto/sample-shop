package com.emanueldev.sample_shop.domain.mappers;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.dtos.response.ProductResponseDTO;
import com.emanueldev.sample_shop.model.Product;


public class ProductMapper {

    public static ProductResponseDTO mappingFromEntityToProductResponseDto(Product data) {
        return ProductResponseDTO
                .builder()
                .productId(data.getId())
                .name(data.getName())
                .description(data.getDescription())
                .price(data.getPrice())
                .stockQuantity(data.getStockQuantity())
                .build();
    }

    public static Product mappingFromProductRequestToProductEntity(ProductRequestDTO data) {
        return Product
                .builder()
                .name(data.getName())
                .description(data.getDescription())
                .price(data.getPrice())
                .stockQuantity(data.getStockQuantity())
                .build();
    }
}
