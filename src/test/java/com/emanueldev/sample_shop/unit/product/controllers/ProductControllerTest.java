package com.emanueldev.sample_shop.unit.product.controllers;

import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.ProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.mapper.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.services.products.*;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateProductUseCase createProductUseCase;

    @MockitoBean
    GetAllProductsUseCase getAllProductsUseCase;

    @MockitoBean
    GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean
    UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    ProductMapper productMapper;

    Product product;

    ProductRequestDTO productRequestDTO;

    ProductResponseDTO productResponseDTO;

    UUID productId;

    @BeforeEach
    void setup() {
        productId = UUID.fromString("3492e3ef-e7de-4c18-81dd-8a814e43d54f");
        productRequestDTO = new ProductRequestDTO(
                "Garrafa térmica stanley",
                "Garrafa boa para o verão.",
                new BigDecimal("100.00"),
                20L);
        product = Product
                .builder()
                .id(productId)
                .name("Garrafa térmica stanley")
                .description("Garrafa boa para o verão.")
                .price(new BigDecimal("100.00"))
                .stockQuantity(20L)
                .build();

        productResponseDTO = ProductResponseDTO
                .builder()
                .id(productId)
                .name("Garrafa térmica stanley")
                .description("Garrafa boa para o verão.")
                .price(new BigDecimal("100.00"))
                .stockQuantity(20L)
                .build();
    }

    @DisplayName("Given Product Object When Create Product then Return Saved Product")
    @Test
    void testGivenProductObject_WhenCreateProduct_thenReturnSavedProduct() throws Exception {
        given(createProductUseCase.execute(any(ProductRequestDTO.class))).willReturn(product);

            given(productMapper.mappingFromProductRequestToProductEntity(productRequestDTO)).willReturn(product);
            given(productMapper.mappingFromEntityToProductResponseDto(product)).willReturn(productResponseDTO);
            ResultActions response = mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequestDTO)));

            response.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(productRequestDTO.getName())))
                    .andExpect(jsonPath("$.id", is(productId.toString())))
                    .andExpect(jsonPath("$.description", is(productRequestDTO.getDescription())))
                    .andExpect(jsonPath("$.price", is(productRequestDTO.getPrice().doubleValue())))
                    .andExpect(jsonPath("$.stockQuantity", is(20)));

    }

    @DisplayName("Given Product Name That Already Exists When Create Product then Throws UnprocessableEntity Exception")
    @Test
    void testGivenEmptyRequestBody_WhenCreateProduct_thenThrowsUnprocessableEntityException() throws Exception {
        String expectedPath = "/products";
        String expectedMethod = "POST";
        String expectedStatus = "Unprocessable Entity";
        int expectedCode = 422;
        ProductRequestDTO nullRequestBody = new ProductRequestDTO(
                null,
                null,
                null,
                null);


        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullRequestBody)));

        response.andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.code", is(expectedCode)));

    }


    @DisplayName("Given Product Name That Already Exists When Create Product then Throws BadRequest Exception")
    @Test
    void testGivenProductNameThatAlreadyExists_WhenCreateProduct_thenThrowsBadRequestException() throws Exception {
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

        @DisplayName("Given List of Products When findAll Products then Return Products List")
        @Test
        void testGivenListOfProducts_whenFindAllProducts_ThenReturnProductsList() throws Exception {
           int pageNumber = 0;
           int size = 5;
           UUID productId2 = UUID.fromString("57922257-3e13-438d-8e83-21cab11a7f41");

            Product product2 =  Product
                    .builder()
                    .id(productId2)
                    .name("Garrafa térmica stanley")
                    .description("Garrafa boa para o verão.")
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(20L)
                    .build();

            List<Product> productsList = List.of(product, product2);

            Pageable pageable = PageRequest.of(pageNumber, size);

            Page<Product> productPage = new PageImpl<>(productsList, pageable, productsList.size());

            List<ProductResponseDTO> productResponseDTOList =
                        productsList
                                .stream()
                                .map(product ->
                                     ProductResponseDTO
                                            .builder()
                                            .id(product.getId())
                                            .name(product.getName())
                                            .description(product.getDescription())
                                            .price(product.getPrice())
                                            .stockQuantity(product.getStockQuantity())
                                            .build()
                                ).toList();

            PaginatedProductResponseDTO paginatedProductResponseDTO = PaginatedProductResponseDTO
                    .builder()
                    .products(productResponseDTOList)
                    .current_page(productPage.getNumber())
                    .total_items(productPage.getTotalElements())
                    .total_pages(productPage.getTotalPages())
                    .build();


                given(getAllProductsUseCase.execute(pageNumber, size)).willReturn(productPage);
                given(productMapper.mappingFromProductPageToPaginatedProductDTO(productPage)).willReturn(paginatedProductResponseDTO);

                ResultActions response = mockMvc.perform(get("/products"));

                response.andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.products[0].id", is(product.getId().toString())))
                        .andExpect(jsonPath("$.products[0].name", is(product.getName())))
                        .andExpect(jsonPath("$.products[0].description", is(product.getDescription())))
                        .andExpect(jsonPath("$.products[0].price", is(product.getPrice().doubleValue())))
                        .andExpect(jsonPath("$.products[0].stockQuantity", is(product.getStockQuantity().intValue())))

                        .andExpect(jsonPath("$.products[1].id", is(product2.getId().toString())))
                        .andExpect(jsonPath("$.products[1].name", is(product2.getName())))
                        .andExpect(jsonPath("$.products[1].description", is(product2.getDescription())))
                        .andExpect(jsonPath("$.products[1].price", is(product2.getPrice().doubleValue())))
                        .andExpect(jsonPath("$.products[1].stockQuantity", is(product2.getStockQuantity().intValue())))

                        .andExpect(jsonPath("$.current_page", is(pageNumber)))
                        .andExpect(jsonPath("$.total_items", is(productsList.size())))
                        .andExpect(jsonPath("$.total_pages", is(paginatedProductResponseDTO.getTotal_pages())));
        }

        @DisplayName("Given ProductId When findById then Return Product Object")
        @Test
        void testGivenProductId_whenFindById_thenReturnProductObject() throws Exception {
            given(getProductByIdUseCase.execute(productId)).willReturn(product);
            given(productMapper.mappingFromEntityToProductResponseDto(product)).willReturn(productResponseDTO);

            ResultActions response = mockMvc.perform(get("/products/{id}", productId));


            response.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(productResponseDTO.getId().toString())))
                    .andExpect(jsonPath("$.name", is(productResponseDTO.getName())))
                    .andExpect(jsonPath("$.description", is(productResponseDTO.getDescription())))
                    .andExpect(jsonPath("$.price", is(productResponseDTO.getPrice().doubleValue())))
                    .andExpect(jsonPath("$.stockQuantity", is(productResponseDTO.getStockQuantity().intValue())));
        }

    @DisplayName("Given ProductId Of An Product That Not Exists When findById then Throws Exception")
    @Test
    void testGivenProductIdOfAnProductThatNotExists_WhenFindById_thenThrowsException () throws Exception {
        String id = productId.toString();
        given(getProductByIdUseCase.execute(productId)).willThrow(new HttpNotFoundException(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND));
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "GET";
        int expectedCode = 404;
        String expectedStatus = "Not Found";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;

        ResultActions response = mockMvc.perform(get("/products/{id}", productId));

        response.
                andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @DisplayName("Given ProductId Of An Product That Not Exists When findById then Return Not Found")
    @Test
    void testGivenInvalidProductId_WhenFindById_thenThrowsUnprocessableEntityException () throws Exception {
        String id = "invalidId";
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "GET";
        int expectedCode = 422;
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalidId";

        ResultActions response = mockMvc.perform(get("/products/{id}", id));

        response
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @DisplayName("Given Update Product When Update then Return Updated Product Object")
    @Test
    void testGivenProduct_whenUpdate_thenReturnProductObject () throws Exception {
        String id = productId.toString();
        var updateProductRequest = new ProductRequestDTO(
                "Garrafa térmica stanley atualizada",
                "Garrafa boa para o verão atualizado.",
                new BigDecimal("100.30"),
                5L);

        var updatedProduct = Product
                .builder()
                .id(productId)
                .name(updateProductRequest.getName())
                .description(updateProductRequest.getDescription())
                .price(updateProductRequest.getPrice())
                .stockQuantity(updateProductRequest.getStockQuantity())
                .build();

        var productResponse = ProductResponseDTO
                .builder()
                .id(productId)
                .name(updateProductRequest.getName())
                .description(updateProductRequest.getDescription())
                .price(updateProductRequest.getPrice())
                .stockQuantity(updateProductRequest.getStockQuantity())
                .build();


            given(updateProductUseCase.execute(eq(productId), any(ProductRequestDTO.class))).willReturn(updatedProduct);
            given(productMapper.mappingFromEntityToProductResponseDto(any(Product.class))).willReturn(productResponse);


           ResultActions response = mockMvc.perform(put("/products/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateProductRequest))
            );


           response
                   .andExpect(status().isOk())
                   .andDo(print())
                   .andExpect(jsonPath("$.id", is(id)))
                   .andExpect(jsonPath("$.name", is(updatedProduct.getName())))
                   .andExpect(jsonPath("$.description", is(updatedProduct.getDescription())))
                   .andExpect(jsonPath("$.price", is(updatedProduct.getPrice().doubleValue())))
                   .andExpect(jsonPath("$.stockQuantity", is(updatedProduct.getStockQuantity().intValue())));
    }

    @DisplayName("Given Invalid ProductId When Update then Throws Unprocessable Entity Exception")
    @Test
    void testGivenInvalidProductId_whenUpdate_thenThrowsUnprocessableEntityException () throws Exception {
        String id = "invalidId";
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "PUT";
        int expectedCode = 422;
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalidId";

        var updateProductRequest = new ProductRequestDTO(
                "Garrafa térmica stanley atualizada",
                "Garrafa boa para o verão atualizado.",
                new BigDecimal("130.30"),
                5L);



        ResultActions response = mockMvc.perform(put("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProductRequest))
        );


        response
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @DisplayName("Given Product Name Of Other Existent Product When Update then Throws Unprocessable Entity Exception")
    @Test
    void testGivenProductNameOfOtherExistentProduct_whenUpdate_thenThrowsBadRequestException () throws Exception {
        String id = productId.toString();
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "PUT";
        int expectedCode = 400;
        String expectedStatus = "Bad Request";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS;

        var updateProductRequest = new ProductRequestDTO(
                "Garrafa térmica stanley atualizada",
                "Garrafa boa para o verão atualizado.",
                new BigDecimal("130.30"),
                5L);


        given(updateProductUseCase.execute(eq(productId), any(ProductRequestDTO.class))).willThrow(new HttpBadRequestException(
                ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS));


        ResultActions response = mockMvc.perform(put("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProductRequest))
        );


        response
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }


    @DisplayName("Given ProductId Of An Product That Not Exists When Update then Throws Unprocessable Entity Exception")
    @Test
    void testGivenProductIdOfAnProductThatNotExists_whenUpdate_thenThrowsNotFoundException () throws Exception {
        String id = productId.toString();
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "PUT";
        int expectedCode = 404;
        String expectedStatus = "Not Found";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;

        var updateProductRequest = new ProductRequestDTO(
                "Garrafa térmica stanley atualizada",
                "Garrafa boa para o verão atualizado.",
                new BigDecimal("130.30"),
                5L);


        given(updateProductUseCase.execute(eq(productId), any(ProductRequestDTO.class))).willThrow(new HttpNotFoundException(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND));


        ResultActions response = mockMvc.perform(put("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProductRequest))
        );


        response
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }


    @DisplayName("Given ProductId When Delete then Return NotContent")
    @Test
    void testGivenProductId_whenDelete_thenReturnNotContent () throws Exception {
        willDoNothing().given(deleteProductUseCase).execute(productId);

        ResultActions response = mockMvc.perform(delete("/products/{id}", productId));


        response.andDo(print())
                .andExpect(status().isNoContent());
    }


    @DisplayName("Given ProductId Of An Product That Not Exists When Delete then Throws NotFound Exception")
    @Test
    void testGivenProductIdOfAnProductThatNotExists_whenDelete_thenThrowsNotFoundException () throws Exception {
        String id = productId.toString();
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "DELETE";
        int expectedCode = 404;
        String expectedStatus = "Not Found";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;


        willThrow(new HttpNotFoundException(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND)).given(deleteProductUseCase).execute(productId);

        ResultActions response = mockMvc.perform(delete("/products/{id}", productId));


        response
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @DisplayName("Given ProductId Of An Product That Not Exists When Delete then Throws NotFound Exception")
    @Test
    void testGivenInvalidProductId_whenDelete_thenThrowsUnprocessableEntity () throws Exception {
        String id = "invalidId";
        String expectedPath = String.format("/products/%s", id);
        String expectedMethod = "DELETE";
        int expectedCode = 422;
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalidId";



        ResultActions response = mockMvc.perform(delete("/products/{id}", id));


        response
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(jsonPath("$.path", is(expectedPath)))
                .andExpect(jsonPath("$.method", is(expectedMethod)))
                .andExpect(jsonPath("$.code", is(expectedCode)))
                .andExpect(jsonPath("$.status", is(expectedStatus)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }
}
