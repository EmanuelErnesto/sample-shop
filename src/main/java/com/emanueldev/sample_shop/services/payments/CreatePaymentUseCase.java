package com.emanueldev.sample_shop.services.payments;

import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.models.Payment;
import com.emanueldev.sample_shop.repositories.OrderRepository;
import com.emanueldev.sample_shop.repositories.PaymentRepository;
import com.emanueldev.sample_shop.utils.OrderExceptionMessageUtils;
import com.emanueldev.sample_shop.utils.PaymentExceptionMessageUtils;
import com.emanueldev.sample_shop.utils.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;


    public CreatePaymentUseCase(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository

    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment execute(UUID orderId) {
        OrderModel order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new HttpNotFoundException(
                                OrderExceptionMessageUtils.ORDER_NOT_FOUND));

        if(order.getPayment() != null) {
            throw new HttpBadRequestException(PaymentExceptionMessageUtils.PAYMENT_ALREADY_EXISTS);
        }

        Payment payment = Payment
                .builder()
                .order(order)

                .amount(order.getTotalValue())
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        order.setPayment(payment);

        orderRepository.save(order);

        return payment;
    }
}
