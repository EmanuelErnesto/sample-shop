package com.emanueldev.sample_shop.controllers;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.dtos.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.dtos.response.ProductResponseDTO;
import com.emanueldev.sample_shop.domain.mappers.ProductMapper;
import com.emanueldev.sample_shop.model.Product;
import com.emanueldev.sample_shop.services.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            GetAllProductsUseCase getAllProductsUseCase,
            DeleteProductUseCase deleteProductUseCase,
            UpdateProductUseCase updateProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO body) {
        Product product = createProductUseCase.execute(body);

        ProductResponseDTO response = ProductMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable("id") UUID id) {
        Product product = getProductByIdUseCase.execute(id);

        ProductResponseDTO response = ProductMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PaginatedProductResponseDTO> getAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer pageNumber,
            @RequestParam(defaultValue = "5") @PositiveOrZero final Integer pageSize
    ) {
        Page<Product> productPage = getAllProductsUseCase.execute(pageNumber, pageSize);

        PaginatedProductResponseDTO response = ProductMapper
                .mappingFromProductPageToPaginatedProductDTO(productPage);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID id
    ){
        deleteProductUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid ProductRequestDTO productRequestDTO
    ) {
        Product product = updateProductUseCase.execute(id, productRequestDTO);

        ProductResponseDTO response = ProductMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.ok(response);
    }
}
