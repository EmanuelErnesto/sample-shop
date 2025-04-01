package com.emanueldev.sample_shop.unit.product.services;

import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.services.products.GetAllProductsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GetAllProductsUseCaseTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    GetAllProductsUseCase getAllProductsUseCase;

    Product product1;

    Product product2;

    @BeforeEach
    void setup () {
        product1 = Product
                .builder()
                .name("Notebook ideapad 3i")
                .description("Notebook para gamers exigentes.")
                .price(new BigDecimal("3000.00"))
                .stockQuantity(1L)
                .build();

        product2 = Product
                .builder()
                .name("Notebook lenovo loq rtx 2050")
                .description("Notebook voltado ao público gamer.")
                .price(new BigDecimal("4140.00"))
                .stockQuantity(10L)
                .build();
    }

    @DisplayName("Given Products List When findAll Products then Return Products List")
    @Test
    void testGivenProductsList_WhenFindAllProducts_thenReturnProductsList() {
        int pageNumber = 0;
        int pageSize = 5;
        int expectedSize = 2;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Product> products = List.of(product1, product2);

        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        given(productRepository.findAll(pageable)).willReturn(productPage);

        Page<Product> result = getAllProductsUseCase.execute(pageNumber, pageSize);

        assertNotNull(result);
        assertInstanceOf(Page.class, result);
        assertEquals(expectedSize, result.getTotalElements());
        assertTrue(result.getContent().contains(product1));
        assertTrue(result.getContent().contains(product2));
    }

    @DisplayName("Given Empty Products List When findAll Products then Return Empty Products List")
    @Test
    void testGivenEmptyProductsList_WhenFindAllProducts_thenReturnEmptyProductsList() {
        int pageNumber = 0;
        int pageSize = 5;
        int expectedSize = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Product> products = List.of();

        Page<Product> productPage = new PageImpl<>(products, pageable,0);

        given(productRepository.findAll(pageable)).willReturn(productPage);

        Page<Product> result = getAllProductsUseCase.execute(pageNumber, pageSize);

        assertNotNull(result);
        assertInstanceOf(Page.class, result);
        assertEquals(expectedSize, result.getTotalElements());
    }


}
