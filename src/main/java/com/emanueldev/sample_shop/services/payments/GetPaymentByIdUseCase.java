package com.emanueldev.sample_shop.services.payments;

import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.Payment;
import com.emanueldev.sample_shop.repositories.PaymentRepository;
import com.emanueldev.sample_shop.utils.OrderExceptionMessageUtils;
import com.emanueldev.sample_shop.utils.PaymentExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetPaymentByIdUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentByIdUseCase(
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public Payment execute(UUID id) {
        return paymentRepository
                .findById(id)
                .orElseThrow(() ->
                        new HttpNotFoundException(
                                PaymentExceptionMessageUtils.PAYMENT_NOT_FOUND));
    }
}
