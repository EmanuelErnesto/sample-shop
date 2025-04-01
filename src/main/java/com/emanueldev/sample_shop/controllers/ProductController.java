package com.emanueldev.sample_shop.controllers;

import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.ProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.mapper.ProductMapper;
import com.emanueldev.sample_shop.exceptions.ApplicationException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.services.products.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;


@Tag(name = "Products", description = "Endpoints for Product CRUD operations like Create Product, Get Paginated Products, Update Product and Delete Product")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ProductMapper productMapper;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            GetAllProductsUseCase getAllProductsUseCase,
            DeleteProductUseCase deleteProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            ProductMapper productMapper
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.productMapper = productMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Resource for create a new product",
        responses = {
                @ApiResponse(responseCode = "201", description = "Product created successfully.",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                @ApiResponse(responseCode = "422", description = "Invalid field inserted.",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                @ApiResponse(responseCode = "400", description = "ProductName already used",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
        })
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO body) {
        Product product = createProductUseCase.execute(body);

        ProductResponseDTO response = productMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @Operation(summary = "Return a list of paginated products", description = "Resource that return a list of products",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedProductResponseDTO.class))),
            })
    @GetMapping
    public ResponseEntity<PaginatedProductResponseDTO> getAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer pageNumber,
            @RequestParam(defaultValue = "5") @PositiveOrZero final Integer pageSize
    ) {
        Page<Product> productPage = getAllProductsUseCase.execute(pageNumber, pageSize);

        PaginatedProductResponseDTO response = productMapper
                .mappingFromProductPageToPaginatedProductDTO(productPage);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Show a existent product", description = "Resource that return a existent product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))
                    ),
            })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable("id") UUID id) {
        Product product = getProductByIdUseCase.execute(id);

        ProductResponseDTO response = productMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Delete a existent product", description = "Resource that delete a existent product",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id inserted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID id
    ){
        deleteProductUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a existent product", description = "Resource that can update a existent product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
            })

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid ProductRequestDTO productRequestDTO
    ) {
        Product product = updateProductUseCase.execute(id, productRequestDTO);

        ProductResponseDTO response = productMapper
                .mappingFromEntityToProductResponseDto(product);

        return ResponseEntity.ok(response);
    }
}
