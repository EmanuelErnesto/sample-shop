package com.emanueldev.sample_shop.repositories;

import com.emanueldev.sample_shop.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
