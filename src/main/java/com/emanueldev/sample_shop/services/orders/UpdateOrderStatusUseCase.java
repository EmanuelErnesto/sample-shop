package com.emanueldev.sample_shop.services.orders;

import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.models.OrderStatus;
import com.emanueldev.sample_shop.repositories.OrderRepository;
import com.emanueldev.sample_shop.utils.OrderExceptionMessageUtils;
import com.emanueldev.sample_shop.utils.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;

    public UpdateOrderStatusUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void execute(UUID id) {
        OrderModel order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new HttpNotFoundException(OrderExceptionMessageUtils.ORDER_NOT_FOUND));

        validateOrderStatus(order);

        order.setOrderStatus(OrderStatus.DELIVERED);

        orderRepository.save(order);

    }

    private void validateOrderStatus (OrderModel order) {
        if (order.getPayment().getPaymentStatus() != PaymentStatus.PAID) {
            throw new HttpBadRequestException(OrderExceptionMessageUtils.ORDER_NOT_PAYED);
        }

        if(order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new HttpBadRequestException(OrderExceptionMessageUtils.ORDER_CANCELED);
        }
    }

}
