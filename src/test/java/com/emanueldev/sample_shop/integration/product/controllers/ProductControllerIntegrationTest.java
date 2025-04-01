package com.emanueldev.sample_shop.integration.product.controllers;

import com.emanueldev.sample_shop.config.TestConfigs;
import com.emanueldev.sample_shop.config.TestContainersConfig;
import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.PaginatedProductResponseDTO;
import com.emanueldev.sample_shop.domain.products.dto.response.ProductResponseDTO;
import com.emanueldev.sample_shop.exceptions.ApplicationException;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProductControllerIntegrationTest extends TestContainersConfig {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static ProductRequestDTO productRequestDTO;
    private static ProductResponseDTO productResponseDTO;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/api/v1/products")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        productRequestDTO = new ProductRequestDTO("Fogão 4 bocas", "Fogão de última geração.", new BigDecimal("299.99"), 40L);
    }

    @Order(1)
    @Test
    @DisplayName("Given Valid ProductRequestDTO Object When Create One Product Should Return A Product Object")
    void integrationTestGivenValidProductRequestDTOObject_when_CreateOneProduct_ShouldReturnAProductObject() throws JsonProcessingException {
        String content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(productRequestDTO)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        ProductResponseDTO createdProduct = objectMapper.readValue(content, ProductResponseDTO.class);

        productResponseDTO = createdProduct;

        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId());
        assertNotNull(createdProduct.getName());
        assertNotNull(createdProduct.getDescription());
        assertNotNull(createdProduct.getPrice());
        assertNotNull(createdProduct.getStockQuantity());

        assertEquals(productRequestDTO.getName(), createdProduct.getName());
        assertEquals(productRequestDTO.getDescription(), createdProduct.getDescription());
        assertEquals(productRequestDTO.getPrice(), createdProduct.getPrice());
        assertEquals(productRequestDTO.getStockQuantity(), createdProduct.getStockQuantity());
    }

    @Order(3)
    @Test
    @DisplayName("Given Body With Null Values When Create One Product Should Throws Unprocessable Entity Exception")
    void integrationTestGivenBodyWithNullValues_when_CreateOneProduct_ShouldThrowsUnprocessableEntityException() throws JsonProcessingException {
        String expectedPath = "/api/v1/products";
        String expectedMethod = "POST";
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Validation error";
        int expectedCode = 422;


        String content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(new ProductRequestDTO(null, null, null, null))
                .when()
                .post()
                .then()
                .statusCode(422)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);


        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }


    @Order(2)
    @Test
    @DisplayName("Given ProductId When Get Product By Id Should Return Product Response DTO")
    void integrationTestGivenProductId_when_GetProductById_ShouldReturnProduct() throws JsonProcessingException {
        UUID productId = productResponseDTO.getId();

        String content = given().spec(specification)
                .when()
                .get("/{id}", productId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        ProductResponseDTO response = objectMapper.readValue(content, ProductResponseDTO.class);


        assertNotNull(response);

        assertEquals(productResponseDTO.getId(), response.getId());
        assertEquals(productResponseDTO.getName(), response.getName());
        assertEquals(productResponseDTO.getDescription(), response.getDescription());
        assertEquals(productResponseDTO.getPrice(), response.getPrice());
        assertEquals(productResponseDTO.getStockQuantity(), response.getStockQuantity());

        assertInstanceOf(ProductResponseDTO.class, response);

    }

    @Order(4)
    @Test
    @DisplayName("Given Invalid ProductId When Get Product By Id Should Throws Unprocessable Entity Exception")
    void integrationTestGivenInvalidProductId_when_GetProductById_ShouldThrowsUnprocessableEntityException() throws JsonProcessingException {
        String invalidProductId = "invalidProductId";
        String expectedPath = String.format("/api/v1/products/%s", invalidProductId);
        String expectedMethod = "GET";
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalidProductId";
        int expectedCode = 422;


        String content = given().spec(specification)
                .when()
                .get("/{id}", invalidProductId)
                .then()
                .statusCode(422)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);

        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }

    @Order(5)
    @Test
    @DisplayName("Given ProductId Of An Product That Not Exists When Get Product By Id Should Throws NotFound Exception")
    void integrationTestGivenProductIdOfAnProductThatNotExists_when_GetProductById_ShouldThrowsNotFoundException() throws JsonProcessingException {
        String invalidProductId = "fbae657e-a469-4429-94cf-0bd15abb64e4";
        String expectedPath = String.format("/api/v1/products/%s", invalidProductId);
        String expectedMethod = "GET";
        String expectedStatus = "Not Found";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;
        int expectedCode = 404;

        String content = given().spec(specification)
                .when()
                .get("/{id}", invalidProductId)
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);


        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);
    }

    @Order(6)
    @Test
    @DisplayName("Given PageNumber and PageSize When Get All Products Should Return Paginated Products List")
    void integrationTestGivenPageNumberAndPageSize_when_GetAllProducts_ShouldReturnPaginatedProductsList() throws JsonProcessingException {
        int pageNumber = 0;
        int pageSize = 5;
        int expectedTotalItems = 1;
        int expectedTotalPages = 1;

        String content = given().spec(specification)
                .when()
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PaginatedProductResponseDTO response = objectMapper.readValue(content, PaginatedProductResponseDTO.class);

        assertNotNull(response);

        assertEquals(productResponseDTO.getId(), response.getProducts().get(0).getId());
        assertEquals(productResponseDTO.getName(), response.getProducts().get(0).getName());
        assertEquals(productResponseDTO.getDescription(), response.getProducts().get(0).getDescription());
        assertEquals(productResponseDTO.getPrice(), response.getProducts().get(0).getPrice());
        assertEquals(productResponseDTO.getStockQuantity(), response.getProducts().get(0).getStockQuantity());

        assertEquals(expectedTotalItems, response.getProducts().size());
        assertEquals(expectedTotalPages, response.getTotal_pages());
        assertEquals(pageNumber, response.getCurrent_page());

        assertInstanceOf(PaginatedProductResponseDTO.class, response);

    }

    @Order(7)
    @Test
    @DisplayName("Given Valid Product Request DTO Object When Update Product Should Return Updated Product")
    void integrationTestGivenValidProductRequestDTOObject_when_UpdateProduct_ShouldReturnUpdatedProduct() throws JsonProcessingException {
        var productRequest = new ProductRequestDTO("updated product", "updated product description", new BigDecimal("100"), 500L);
        UUID productId = productResponseDTO.getId();

        String content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(productRequest)
                .when()
                .put("/{id}", productId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        ProductResponseDTO response = objectMapper.readValue(content, ProductResponseDTO.class);

        assertNotNull(response);
        assertEquals(productId, response.getId());
        assertEquals(productRequest.getName(), response.getName());
        assertEquals(productRequest.getDescription(), response.getDescription());
        assertEquals(productRequest.getPrice(), response.getPrice());
        assertEquals(productRequest.getStockQuantity(), response.getStockQuantity());

        assertInstanceOf(ProductResponseDTO.class, response);
    }

    @Order(8)
    @Test
    @DisplayName("Given Product Id Of Product That Not Exists When Update Product Should Throws NotFound Exception")
    void integrationTestGivenProductIdOfProductThatNotExists_when_UpdateProduct_ShouldThrowsNotFoundException() throws JsonProcessingException {
        String productId = "25e2fa6f-2afe-4cfe-bb1f-29d294ca33ce";

        String expectedPath = String.format("/api/v1/products/%s", productId);
        String expectedMethod = "PUT";
        String expectedStatus = "Not Found";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;
        int expectedCode = 404;

        var productRequest = new ProductRequestDTO("updated product 2", "updated product description 2", new BigDecimal("100"), 50L);


        String content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(productRequest)
                .when()
                .put("/{id}", productId)
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);


        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }

    @Order(9)
    @Test
    @DisplayName("Given Body With Null Values When Update One Product Should Throws Unprocessable Entity Exception")
    void integrationTestGivenBodyWithNullValues_when_UpdateOneProduct_ShouldThrowsUnprocessableEntityException() throws JsonProcessingException {
        UUID productId = productResponseDTO.getId();
        String expectedPath = String.format("/api/v1/products/%s", productId);
        String expectedMethod = "PUT";
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Validation error";
        int expectedCode = 422;


        String content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(new ProductRequestDTO(null, null, null, null))
                .when()
                .put("/{id}", productId)
                .then()
                .statusCode(422)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);


        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }

    @Order(10)
    @Test
    @DisplayName("Given Invalid ProductId When Update Product Should Throws Unprocessable Entity Exception")
    void integrationTestGivenInvalidProductId_when_UpdateProduct_ShouldThrowsUnprocessableEntityException() throws JsonProcessingException {
        String invalidProductId = "invalidProductId";
        String expectedPath = String.format("/api/v1/products/%s", invalidProductId);
        String expectedMethod = "PUT";
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalidProductId";
        int expectedCode = 422;


        String content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(new ProductRequestDTO(null, null, null, null))
                .when()
                .put("/{id}", invalidProductId)
                .then()
                .statusCode(422)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);

        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }


    @Order(11)
    @Test
    @DisplayName("Given ProductId When Delete Product Should Return Empty Body")
    void integrationTestGivenProductId_when_DeleteProduct_ShouldReturnProduct(){
        UUID productId = productResponseDTO.getId();

        given().spec(specification)
                .when()
                .delete("/{id}", productId)
                .then()
                .statusCode(204);

    }

    @Order(12)
    @Test
    @DisplayName("Given ProductId Of A Product That Not Exists When Delete Product Should Throws NotFound Exception")
    void integrationTestGivenProductIdOfAProductThatNotExists_when_DeleteProduct_ShouldThrowsNotFoundException() throws JsonProcessingException {
        UUID productId = productResponseDTO.getId();
        String expectedPath = String.format("/api/v1/products/%s", productId);
        String expectedMethod = "DELETE";
        String expectedStatus = "Not Found";
        String expectedMessage = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;
        int expectedCode = 404;


        String content = given().spec(specification)
                .when()
                .delete("/{id}", productId)
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();


        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);

        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }


    @Order(13)
    @Test
    @DisplayName("Given Invalid ProductId When Delete Product Should Throws Unprocessable Entity Exception")
    void integrationTestGivenInvalidProductId_when_DeleteProduct_ShouldThrowsUnprocessableEntityException() throws JsonProcessingException {
        String invalidProductId = "invalidProductId";
        String expectedPath = String.format("/api/v1/products/%s", invalidProductId);
        String expectedMethod = "DELETE";
        String expectedStatus = "Unprocessable Entity";
        String expectedMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalidProductId";
        int expectedCode = 422;


        String content = given().spec(specification)
                .when()
                .delete("/{id}", invalidProductId)
                .then()
                .statusCode(422)
                .extract()
                .body()
                .asString();

        ApplicationException response = objectMapper.readValue(content, ApplicationException.class);

        assertNotNull(response);

        assertEquals(expectedPath, response.getPath());
        assertEquals(expectedMethod, response.getMethod());
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());

        assertInstanceOf(ApplicationException.class, response);

    }

}
