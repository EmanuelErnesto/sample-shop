package com.emanueldev.sample_shop.unit.product.controllers;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.dtos.response.ProductResponseDTO;
import com.emanueldev.sample_shop.domain.mappers.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.model.Product;
import com.emanueldev.sample_shop.services.*;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private GetAllProductsUseCase getAllProductsUseCase;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    private Product product;

    private ProductRequestDTO productRequestDTO;

    private ProductResponseDTO productResponseDTO;

    private UUID productId;

    @BeforeEach
    void setup() {
        productId = UUID.fromString("3492e3ef-e7de-4c18-81dd-8a814e43d54f");
        productRequestDTO = new ProductRequestDTO(
                "Garrafa térmica stanley",
                "Garrafa boa para o verão.",
                100.00D,
                20L);
        product = Product
                .builder()
                .id(productId)
                .name("Garrafa térmica stanley")
                .description("Garrafa boa para o verão.")
                .price(100.00D)
                .stockQuantity(20L)
                .build();

        productResponseDTO = ProductResponseDTO
                .builder()
                .productId(productId)
                .name("Garrafa térmica stanley")
                .description("Garrafa boa para o verão.")
                .price(100.00D)
                .stockQuantity(20L)
                .build();
    }

    @DisplayName("Given Product Object When Create Product then Return Saved Product")
    @Test
    void testGivenProductObject_WhenCreateProduct_thenReturnSavedProduct() throws Exception {
        given(createProductUseCase.execute(any(ProductRequestDTO.class))).willReturn(product);

        try(MockedStatic<ProductMapper> mockedStatic = Mockito.mockStatic(ProductMapper.class)) {
            mockedStatic.when(() -> ProductMapper.mappingFromProductRequestToProductEntity(productRequestDTO)).thenReturn(product);
            mockedStatic.when(() -> ProductMapper.mappingFromEntityToProductResponseDto(product)).thenReturn(productResponseDTO);
            ResultActions response = mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequestDTO)));

            response.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(productRequestDTO.getName())))
                    .andExpect(jsonPath("$.productId", is(productId.toString())))
                    .andExpect(jsonPath("$.description", is(productRequestDTO.getDescription())))
                    .andExpect(jsonPath("$.price", is(productRequestDTO.getPrice())))
                    .andExpect(jsonPath("$.stockQuantity", is(20)));
        }
    }

    @DisplayName("Given Product Object When Create Product then Return Saved Product")
    @Test
    void testGivenProductNameThatAlreadyExists_WhenCreateProduct_thenThrowsAnException() throws Exception {
        String expectedPath = "/products";
        String expectedMethod = "POST";
        String expectedStatus = "Bad Request";
        int expectedCode = 400;
        String expectedMessage = "A product with same name already exists.";

        given(createProductUseCase.execute(any(ProductRequestDTO.class))).willThrow(
                new HttpBadRequestException(
                ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS));

            ResultActions response = mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequestDTO)));

            response.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.path", is(expectedPath)))
                    .andExpect(jsonPath("$.method", is(expectedMethod)))
                    .andExpect(jsonPath("$.status", is(expectedStatus)))
                    .andExpect(jsonPath("$.code", is(expectedCode)))
                    .andExpect(jsonPath("$.message", is(expectedMessage)));

        }
}
