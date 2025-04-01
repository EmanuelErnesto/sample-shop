package com.emanueldev.sample_shop.services.products;

import com.emanueldev.sample_shop.domain.products.dto.request.ProductRequestDTO;
import com.emanueldev.sample_shop.domain.products.mapper.ProductMapper;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public UpdateProductUseCase(
            ProductRepository productRepository,
            ProductMapper productMapper
    ){
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public Product execute(UUID id, ProductRequestDTO productRequestDTO) {
        Product product = this.productRepository
                .findById(id)
                .orElseThrow(() ->
                        new HttpNotFoundException(
                                ProductExceptionMessageUtils.PRODUCT_NOT_FOUND)
                );

        Optional<Product> productNameAlreadyExists = this.productRepository
                .findByName(productRequestDTO.getName());

        boolean productIdIsDifferent = (productNameAlreadyExists.isPresent() &&
                !productNameAlreadyExists.get().getId().equals(product.getId()));

        if(productIdIsDifferent) {
            throw new HttpBadRequestException(
                    ProductExceptionMessageUtils.PRODUCT_WITH_SAME_NAME_ALREADY_EXISTS
            );
        }

        productMapper.mappingProductRequestDTOToExistentProductEntity(productRequestDTO, product);

        return productRepository.save(product);
    }
}
