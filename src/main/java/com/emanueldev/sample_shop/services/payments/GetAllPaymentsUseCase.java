package com.emanueldev.sample_shop.services.payments;

import com.emanueldev.sample_shop.models.Payment;
import com.emanueldev.sample_shop.repositories.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllPaymentsUseCase {

    private final PaymentRepository paymentRepository;

    public GetAllPaymentsUseCase(
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public Page<Payment> execute(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return paymentRepository.findAll(pageable);
    }
}
