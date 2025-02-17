package com.emanueldev.sample_shop.domain.mappers;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.dtos.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.dtos.response.ProductResponseDTO;
import com.emanueldev.sample_shop.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;


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

    public static PaginatedProductResponseDTO mappingFromProductPageToPaginatedProductDTO(Page<Product> productPage){
        List<ProductResponseDTO> listProductResponseDTO = productPage
                .getContent()
                .stream()
                .map(ProductMapper::mappingFromEntityToProductResponseDto)
                .toList();

        return PaginatedProductResponseDTO
                .builder()
                .products(listProductResponseDTO)
                .current_page(productPage.getNumber())
                .total_items(productPage.getTotalElements())
                .total_pages(productPage.getTotalPages())
                .build();
    }

    public static void mappingProductRequestDTOToExistentProductEntity(ProductRequestDTO requestDTO, Product product){
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
    }
}
