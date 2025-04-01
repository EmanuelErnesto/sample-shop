package com.emanueldev.sample_shop.unit.product.mapper;


import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.ProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.mapper.ProductMapper;
import com.emanueldev.sample_shop.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProductMapperTest {

    ProductMapper productMapper;

    @BeforeEach
    void setup () {
        productMapper = new ProductMapper();
    }

    @DisplayName("When Mapping From ProductRequestDTO to Product Should Return a Product Object")
    @Test
    void testMappingFromProductRequestDTOToProduct_ShouldReturnProductObject() {
        String productName = "produto para teste.";
        String productDescription = "produto testado descrição.";
        BigDecimal productPrice = new BigDecimal("200");
        Long stockQuantity = 10L;
        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                productName,
                productDescription,
                productPrice,
                stockQuantity
        );

        Product product = productMapper.mappingFromProductRequestToProductEntity(productRequestDTO);

        assertNotNull(product);
        assertEquals(productName, product.getName());
        assertEquals(productDescription, product.getDescription());
        assertEquals(productPrice, product.getPrice());
        assertEquals(stockQuantity, product.getStockQuantity());
        assertInstanceOf(Product.class, product);
    }

    @DisplayName("When Mapping From Product Entity to ProductResponseDTO Should Return a ProductResponseDTO Object")
    @Test
    void testMappingFromProductEntityToProductResponseDTO_ShouldReturnProductResponseDTOObject() {
        UUID productId = UUID.fromString("a70a7f05-d5e4-4c2b-b96e-0750710d632d");
        String productName = "produto para teste.";
        String productDescription = "produto testado descrição.";
        BigDecimal productPrice = new BigDecimal("200");
        Long stockQuantity = 10L;

        Product product = Product
                .builder()
                .id(productId)
                .name(productName)
                .description(productDescription)
                .price(productPrice)
                .stockQuantity(stockQuantity)
                .build();

        ProductResponseDTO productResponseDTO = productMapper.mappingFromEntityToProductResponseDto(product);


        assertNotNull(productResponseDTO);
        assertEquals(product.getName(), productResponseDTO.getName());
        assertEquals(product.getDescription(), productResponseDTO.getDescription());
        assertEquals(product.getPrice(), productResponseDTO.getPrice());
        assertEquals(product.getStockQuantity(), productResponseDTO.getStockQuantity());
        assertInstanceOf(ProductResponseDTO.class, productResponseDTO);
    }


    @DisplayName("When Mapping From Product Page To Paginated ProductDTO Should Return a PaginatedProductDTO Object")
    @Test
    void testMappingFromProductPageToPaginatedProductDTO_ShouldReturnPaginatedProductDTOObject() {
        UUID productId = UUID.fromString("a70a7f05-d5e4-4c2b-b96e-0750710d632d");
        String productName = "produto para teste.";
        String productDescription = "produto testado descrição.";
        BigDecimal productPrice = new BigDecimal("200");
        Long stockQuantity = 10L;

        Product product = Product
                .builder()
                .id(productId)
                .name(productName)
                .description(productDescription)
                .price(productPrice)
                .stockQuantity(stockQuantity)
                .build();

        Product product2 = Product
                .builder()
                .id(UUID.fromString("de384fc4-4fc0-4f3e-a0a5-fd0468d15a88"))
                .name("product para teste 2")
                .description("produto testado 2 descrição.")
                .price(new BigDecimal("300"))
                .stockQuantity(20L)
                .build();

        List<Product> productsList = List.of(product, product2);

        List<ProductResponseDTO> productResponseDTOList = productsList.stream()
                .map(p ->
                        ProductResponseDTO
                                .builder()
                                .id(p.getId())
                                .name(p.getName())
                                .description(p.getDescription())
                                .price(p.getPrice())
                                .stockQuantity(p.getStockQuantity())
                                .build()).toList();

        Pageable pageable = PageRequest.of(0, 5);

        Page<Product> productPage = new PageImpl<>(productsList, pageable, productsList.size());

        PaginatedProductResponseDTO paginatedProductResponseDTO = productMapper.mappingFromProductPageToPaginatedProductDTO(productPage);

        assertNotNull(paginatedProductResponseDTO);
        assertEquals(productResponseDTOList.size(), paginatedProductResponseDTO.getProducts().size());

        assertEquals(productResponseDTOList.get(0).getId() , paginatedProductResponseDTO.getProducts().get(0).getId() );
        assertEquals(productResponseDTOList.get(0).getName(), paginatedProductResponseDTO.getProducts().get(0).getName());
        assertEquals(productResponseDTOList.get(0).getDescription(), paginatedProductResponseDTO.getProducts().get(0).getDescription());
        assertEquals(productResponseDTOList.get(0).getPrice(), paginatedProductResponseDTO.getProducts().get(0).getPrice());
        assertEquals(productResponseDTOList.get(0).getStockQuantity(), paginatedProductResponseDTO.getProducts().get(0).getStockQuantity());

        assertEquals(productResponseDTOList.get(1).getId() , paginatedProductResponseDTO.getProducts().get(1).getId() );
        assertEquals(productResponseDTOList.get(1).getName(), paginatedProductResponseDTO.getProducts().get(1).getName());
        assertEquals(productResponseDTOList.get(1).getDescription(), paginatedProductResponseDTO.getProducts().get(1).getDescription());
        assertEquals(productResponseDTOList.get(1).getPrice(), paginatedProductResponseDTO.getProducts().get(1).getPrice());
        assertEquals(productResponseDTOList.get(1).getStockQuantity(), paginatedProductResponseDTO.getProducts().get(1).getStockQuantity());

        assertInstanceOf(PaginatedProductResponseDTO.class, paginatedProductResponseDTO);

    }


    @DisplayName("When Mapping ProductRequestDTO To Existent ProductEntity Should Update Product Properties")
    @Test
    void testMappingProductRequestDTOToExistentProductEntity_ShouldUpdateProductProperties() {
        UUID productId = UUID.fromString("7c900ba9-9acc-43bc-8e86-555814b47a0a");
        String productName = "produto para teste.";
        String productDescription = "produto testado descrição.";
        BigDecimal productPrice = new BigDecimal("200.00");
        Long stockQuantity = 10L;

        String updateProductName = "produto atualizado";
        String updateProductDescription = "descrição atualizada";
        BigDecimal updatePrice = new BigDecimal("100.00");
        Long updateStockQuantity = 10L;

        ProductRequestDTO updateProductData = new ProductRequestDTO(
                updateProductName, updateProductDescription, updatePrice, updateStockQuantity
        );

        Product product = Product
                .builder()
                .id(productId)
                .name(productName)
                .description(productDescription)
                .price(productPrice)
                .stockQuantity(stockQuantity)
                .build();

        productMapper.mappingProductRequestDTOToExistentProductEntity(updateProductData, product);


        assertNotNull(product);
        assertEquals(updateProductName, product.getName());
        assertEquals(updateProductDescription, product.getDescription());
        assertEquals(updatePrice, product.getPrice());
        assertEquals(updateStockQuantity, product.getStockQuantity());
        assertInstanceOf(Product.class, product);
    }



}
