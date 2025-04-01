package com.emanueldev.sample_shop.domain.products.mapper;

import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.ProductResponseDTO;
import com.emanueldev.sample_shop.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ProductMapper {

    public ProductResponseDTO mappingFromEntityToProductResponseDto(Product data) {
        return ProductResponseDTO
                .builder()
                .id(data.getId())
                .name(data.getName())
                .description(data.getDescription())
                .price(data.getPrice())
                .stockQuantity(data.getStockQuantity())
                .build();
    }

    public Product mappingFromProductRequestToProductEntity(ProductRequestDTO data) {
        return Product
                .builder()
                .name(data.getName())
                .description(data.getDescription())
                .price(data.getPrice())
                .stockQuantity(data.getStockQuantity())
                .build();
    }

    public PaginatedProductResponseDTO mappingFromProductPageToPaginatedProductDTO(Page<Product> productPage){
        List<ProductResponseDTO> listProductResponseDTO = productPage
                .getContent()
                .stream()
                .map(this::mappingFromEntityToProductResponseDto)
                .toList();

        return PaginatedProductResponseDTO
                .builder()
                .products(listProductResponseDTO)
                .current_page(productPage.getNumber())
                .total_items(productPage.getTotalElements())
                .total_pages(productPage.getTotalPages())
                .build();
    }

    public void mappingProductRequestDTOToExistentProductEntity(ProductRequestDTO requestDTO, Product product){
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
    }
}
