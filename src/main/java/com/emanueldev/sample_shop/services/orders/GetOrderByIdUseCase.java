package com.emanueldev.sample_shop.services.orders;

import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.repositories.OrderRepository;
import com.emanueldev.sample_shop.utils.OrderExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetOrderByIdUseCase {

    private final OrderRepository orderRepository;


    public GetOrderByIdUseCase(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderModel execute(UUID id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new HttpNotFoundException(OrderExceptionMessageUtils.ORDER_NOT_FOUND));
    }

}
