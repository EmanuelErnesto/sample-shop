package com.emanueldev.sample_shop.services.products;

import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    public DeleteProductUseCase(
            ProductRepository productRepository
    ){
        this.productRepository = productRepository;
    }

    @Transactional
    public void execute(UUID id) {
        Product product = this.productRepository
                .findById(id)
                .orElseThrow(() ->
                    new HttpNotFoundException(
                            ProductExceptionMessageUtils.PRODUCT_NOT_FOUND)
                );

        productRepository.delete(product);
    }
}
