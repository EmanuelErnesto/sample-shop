package com.emanueldev.sample_shop.services.products;

import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;

    public GetProductByIdUseCase(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Product execute(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new HttpNotFoundException(
                                ProductExceptionMessageUtils
                                        .PRODUCT_NOT_FOUND));
    }
}
