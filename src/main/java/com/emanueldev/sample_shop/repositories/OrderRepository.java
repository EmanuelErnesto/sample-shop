package com.emanueldev.sample_shop.repositories;

import com.emanueldev.sample_shop.models.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderModel, UUID> {
}
