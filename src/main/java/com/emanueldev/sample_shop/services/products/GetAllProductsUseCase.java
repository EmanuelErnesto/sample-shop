package com.emanueldev.sample_shop.services.products;

import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllProductsUseCase {

    private final ProductRepository productRepository;

    public GetAllProductsUseCase(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> execute(Integer pageNumber, Integer size) {
        return this.productRepository
                .findAll(PageRequest.of(pageNumber, size));
    }

}
