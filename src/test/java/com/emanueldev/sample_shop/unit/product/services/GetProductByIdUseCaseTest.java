package com.emanueldev.sample_shop.unit.product.services;


import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.services.products.GetProductByIdUseCase;
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


import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GetProductByIdUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductByIdUseCase getProductByIdUseCase;

    private Product product;

    @BeforeEach
    void setup() {
        product = Product
                .builder()
                .name("Mesa de madeira")
                .description("Mesa ideal para jantares!.")
                .price(new BigDecimal("199.99"))
                .stockQuantity(4L)
                .build();
    }

    @DisplayName("Given Product ProductId When findById Product then Return Product Object")
    @Test
    void testGiVenProductId_WhenFindById_thenReturnProductObject(){
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));
        UUID productId = UUID.fromString("b16af0b2-7e63-479f-904f-ebd2fa6c3886");

        Product productRetrievedFromDB = getProductByIdUseCase.execute(productId);

        assertNotNull(productRetrievedFromDB);
    }

    @DisplayName("Given Product ProductId Of Non Existent Product When findById Product then Return Product Object")
    @Test
    void testGiVenProductIdOfNonExistentProduct_WhenFindById_thenThrowsException(){
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());
        UUID productId = UUID.fromString("b16af0b2-7e63-479f-904f-ebd2fa6c3886");

        HttpNotFoundException result = assertThrows(HttpNotFoundException.class, () -> {
           getProductByIdUseCase.execute(productId);
        });

        assertEquals(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND, result.getMessage());
        assertInstanceOf(HttpNotFoundException.class, result);
    }

}
