package com.emanueldev.sample_shop.controllers;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.dtos.response.ProductResponseDTO;
import com.emanueldev.sample_shop.domain.mappers.ProductMapper;
import com.emanueldev.sample_shop.model.Product;
import com.emanueldev.sample_shop.services.CreateProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO body) {
        Product product = createProductUseCase.execute(body);

        ProductResponseDTO response = ProductMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
