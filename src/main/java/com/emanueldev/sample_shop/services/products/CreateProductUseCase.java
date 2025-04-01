package com.emanueldev.sample_shop.services.products;

import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.mapper.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public CreateProductUseCase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public Product execute(ProductRequestDTO data) {

        boolean productWithSameNameAlreadyExists = productRepository
                .findByName(data.getName())
                .isPresent();

        if(productWithSameNameAlreadyExists) {
            throw new HttpBadRequestException(ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS);
        }

        Product productToCreate = productMapper
                .mappingFromProductRequestToProductEntity(data);

        return productRepository.save(productToCreate);
    }


}
