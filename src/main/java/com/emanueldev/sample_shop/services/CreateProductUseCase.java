package com.emanueldev.sample_shop.services;

import com.emanueldev.sample_shop.domain.dtos.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.mappers.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.model.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product execute(ProductRequestDTO data) {

        boolean productWithSameNameAlreadyExists = productRepository
                .findByName(data.getName())
                .isPresent();

        if(productWithSameNameAlreadyExists) {
            throw new HttpBadRequestException(ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS);
        }

        Product productToCreate = ProductMapper
                .mappingFromProductRequestToProductEntity(data);

        return productRepository.save(productToCreate);
    }


}
