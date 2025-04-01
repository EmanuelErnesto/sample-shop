package com.emanueldev.sample_shop.unit.product.services;

import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.mapper.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.services.products.UpdateProductUseCase;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    private ProductRequestDTO productRequestDTO;

    private Product product;

    @BeforeEach
    void setup() {
        productRequestDTO = new ProductRequestDTO(
                "Geladeira Eletrolux Atualizada.",
                "Geladeira Eletrolux 2 portas atualizada.",
                new BigDecimal("980.00"),
                10L);
        product = Product
                .builder()
                .name("Geladeira Eletrolux")
                .description("Geladeira Eletrolux 2 portas.")
                .price(new BigDecimal("950.00"))
                .stockQuantity(10L)
                .build();
    }

    @DisplayName("Given ProductRequestDTO Object When Update Product then Return Product Object")
    @Test
    void testGivenProductRequestDTOObject_WhenUpdateProduct_thenReturnProductObject() {
        UUID productId = UUID.fromString("b16af0b2-7e63-479f-904f-ebd2fa6c3886");
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productRepository.findByName(anyString())).willReturn(Optional.empty());
        given(productRepository.save(product)).willReturn(product);
        doAnswer(invocation -> {
            ProductRequestDTO dto = invocation.getArgument(0);
            Product productToUpdate = invocation.getArgument(1);
            productToUpdate.setName(dto.getName());
            productToUpdate.setDescription(dto.getDescription());
            productToUpdate.setPrice(dto.getPrice());
            productToUpdate.setStockQuantity(dto.getStockQuantity());
            return null;
        }).when(productMapper).mappingProductRequestDTOToExistentProductEntity(any(ProductRequestDTO.class), any(Product.class));



        Product updatedProduct = updateProductUseCase.execute(productId, productRequestDTO);

        assertNotNull(updatedProduct);
        assertEquals(productRequestDTO.getName(), product.getName());
        assertEquals(productRequestDTO.getDescription(), product.getDescription());
        assertEquals(productRequestDTO.getPrice(), product.getPrice());
        assertEquals(productRequestDTO.getStockQuantity(), product.getStockQuantity());
    }

    @DisplayName("Given Product Id Of Non Existent When Update Product then Throws An Exception")
    @Test
    void testGivenProductIdOfNonExistentProduct_WhenUpdateProduct_thenThrowsAnException() {
        UUID productId = UUID.fromString("7d40eb46-c0d2-4711-8e94-c73cc1fba505");
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        HttpNotFoundException result = assertThrows(HttpNotFoundException.class, () -> updateProductUseCase.execute(productId, productRequestDTO));

        verify(productRepository, never()).save(any(Product.class));
        assertEquals(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND, result.getMessage());
        assertInstanceOf(HttpNotFoundException.class, result);
    }

    @DisplayName("Given Product Name of Other Product When Update Product then Throws An Exception.")
    @Test
    void testGivenProductNameOfOtherProduct_WhenUpdateProduct_thenThrowsAnException() {
        String nameOfProduct2 = "Geladeira Eletrolux do produto 2";
        productRequestDTO.setName(nameOfProduct2);
        product.setId(UUID.fromString("7d40eb46-c0d2-4711-8e94-c73cc1fba505"));

        var product2 = Product
                .builder()
                .id(UUID.fromString("c8a6188d-6ad5-42d9-aa4d-51f67cb97f85"))
                .name(nameOfProduct2)
                .description("Geladeira Eletrolux 2 portas.")
                .price(new BigDecimal("950.00"))
                .stockQuantity(10L)
                .build();

        UUID productId = UUID.fromString("7d40eb46-c0d2-4711-8e94-c73cc1fba505");

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productRepository.findByName(nameOfProduct2)).willReturn(Optional.of(product2));

        HttpBadRequestException result = assertThrows(HttpBadRequestException.class, () -> updateProductUseCase.execute(productId, productRequestDTO));

        verify(productRepository, never()).save(any(Product.class));
        assertEquals(ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS, result.getMessage());
        assertInstanceOf(HttpBadRequestException.class, result);
    }



}
