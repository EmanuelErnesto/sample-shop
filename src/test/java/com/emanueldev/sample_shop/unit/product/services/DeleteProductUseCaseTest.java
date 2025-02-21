package com.emanueldev.sample_shop.unit.product.services;

import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.model.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.services.DeleteProductUseCase;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteProductUseCaseTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    DeleteProductUseCase deleteProductUseCase;

    private Product product;

    @BeforeEach
    void setup() {
        product = Product
                .builder()
                .name("Mesa de madeira")
                .description("Mesa ideal para jantares!.")
                .price(199.99D)
                .stockQuantity(4L)
                .build();
    }

    @DisplayName("Given ProductId When Delete Product then Do Nothing")
    @Test
    void testGiVenProductId_WhenDeleteProduct_thenDoNothing(){
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));
        UUID productId = UUID.fromString("b16af0b2-7e63-479f-904f-ebd2fa6c3886");

        deleteProductUseCase.execute(productId);

        verify(productRepository, times(1)).delete(product);
    }

    @DisplayName("Given Product ProductId When Delete Product then Do Nothing")
    @Test
    void testGiVenProductIdOfNonExistentProduct_WhenDeleteProduct_thenThrowsAnException(){
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());
        UUID productId = UUID.fromString("b16af0b2-7e63-479f-904f-ebd2fa6c3886");

        HttpNotFoundException result = assertThrows(HttpNotFoundException.class, () -> {
            deleteProductUseCase.execute(productId);
        });

        verify(productRepository, never()).delete(product);
        assertEquals(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND, result.getMessage());
        assertInstanceOf(HttpNotFoundException.class, result);
    }
}
