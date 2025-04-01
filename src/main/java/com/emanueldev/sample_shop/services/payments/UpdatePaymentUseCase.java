package com.emanueldev.sample_shop.services.payments;

import com.emanueldev.sample_shop.clients.PaymentClient;
import com.emanueldev.sample_shop.domain.payments.dtos.response.AuthorizerResponseDTO;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.models.Payment;
import com.emanueldev.sample_shop.repositories.OrderRepository;
import com.emanueldev.sample_shop.repositories.PaymentRepository;
import com.emanueldev.sample_shop.utils.PaymentExceptionMessageUtils;
import com.emanueldev.sample_shop.utils.PaymentStatus;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public UpdatePaymentUseCase(
            PaymentRepository paymentRepository,
            PaymentClient paymentClient,
            OrderRepository orderRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void execute(UUID id) {
        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() ->
                        new HttpNotFoundException(PaymentExceptionMessageUtils.PAYMENT_NOT_FOUND));

        if(payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new HttpBadRequestException(PaymentExceptionMessageUtils.PAYMENT_ALREADY_CREATED);
        }

        try {
            ResponseEntity<AuthorizerResponseDTO> authorizerResponseDTO = paymentClient.validatePaymentAuthorization();

        } catch (FeignException feignException) {
            if(feignException.status() == 403) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new HttpBadRequestException(PaymentExceptionMessageUtils.PAYMENT_NOT_AUTHORIZED);
            }
        }
        payment.setPaymentStatus(PaymentStatus.PAID);
        OrderModel order = payment.getOrder();
        order.setPayment(payment);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
