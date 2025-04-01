package com.emanueldev.sample_shop.repositories;

import com.emanueldev.sample_shop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query
    Optional<Product> findByName(String name);
}
