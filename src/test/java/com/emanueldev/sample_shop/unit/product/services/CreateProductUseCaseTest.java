package com.emanueldev.sample_shop.unit.product.services;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.mappers.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.model.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.services.CreateProductUseCase;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    private ProductRequestDTO productRequestDTO;

    private Product product;

    @BeforeEach
    void setup() {
        productRequestDTO = new ProductRequestDTO(
                "Geladeira Eletrolux",
                "Geladeira Eletrolux 2 portas.",
                950.00D,
                10L);
        product = Product
                .builder()
                .name("Geladeira Eletrolux")
                .description("Geladeira Eletrolux 2 portas.")
                .price(950.00D)
                .stockQuantity(10L)
                .build();
    }

    @DisplayName("Given ProductRequestDTO Object When Save Product then Return Product Object")
    @Test
    void testGivenProductRequestDTOObject_WhenSaveProduct_thenReturnProductObject() {
        given(productRepository.findByName(anyString())).willReturn(Optional.empty());
        given(productRepository.save(product)).willReturn(product);

        Product createdProduct = createProductUseCase.execute(productRequestDTO);

        assertNotNull(createdProduct);
        assertEquals(productRequestDTO.getName(), product.getName());
        assertEquals(productRequestDTO.getDescription(), product.getDescription());
        assertEquals(productRequestDTO.getPrice(), product.getPrice());
        assertEquals(productRequestDTO.getStockQuantity(), product.getStockQuantity());
    }

    @DisplayName("Given Existing Product Name When Save Product then Throws Exception")
    @Test
    void testGivenExistingProductName_WhenSaveProduct_thenThrowsException() {
        given(productRepository.findByName(anyString())).willReturn(Optional.of(product));

        HttpBadRequestException result = assertThrows(HttpBadRequestException.class, () -> {
            createProductUseCase.execute(productRequestDTO);
        });

        verify(productRepository, never()).save(any(Product.class));
        assertEquals(ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS, result.getMessage());
        assertInstanceOf(HttpBadRequestException.class ,result);
    }



  
}