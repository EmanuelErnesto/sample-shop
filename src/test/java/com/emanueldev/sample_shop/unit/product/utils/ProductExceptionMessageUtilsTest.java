package com.emanueldev.sample_shop.unit.product.utils;


import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductExceptionMessageUtilsTest {


    @DisplayName("When Call ProductExceptionMessageUtils constructor Should create a ProductExceptionMessageUtils instance")
    @Test
    void testCallProductExceptionMessageUtilsConstructor_WhenCreatingNewInstance_ShouldReturnProductExceptionMessageUtilsInstance() {
        ProductExceptionMessageUtils productExceptionMessageUtils = new ProductExceptionMessageUtils();

        assertNotNull(productExceptionMessageUtils);
        assertInstanceOf(ProductExceptionMessageUtils.class, productExceptionMessageUtils);
    }


    @DisplayName("When Access Product With Same Name Already Exists property Should return 'A product with same name already exists' message")
    @Test
    void testAccessProductWithSameNameAlreadyExistsProperty_ShouldReturnAProductWithSameNameAlreadyExistsMessage() {
        String expectedMessage = "A product with same name already exists.";

        String result = ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS;

        assertNotNull(result);
        assertEquals(expectedMessage, result);
    }

    @DisplayName("When Access Product Not Found property Should return 'Product Not Found.' message")
    @Test
    void testAccessProductNotFoundProperty_ShouldReturnProductNotFoundMessage() {
        String expectedMessage = "Product Not Found.";

        String result = ProductExceptionMessageUtils.PRODUCT_NOT_FOUND;

        assertNotNull(result);
        assertEquals(expectedMessage, result);
    }



}
