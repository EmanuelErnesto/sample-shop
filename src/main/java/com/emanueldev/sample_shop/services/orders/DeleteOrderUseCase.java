package com.emanueldev.sample_shop.services.orders;

import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.models.OrderStatus;
import com.emanueldev.sample_shop.models.Product;
import com.emanueldev.sample_shop.repositories.OrderRepository;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.OrderExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DeleteOrderUseCase(
            final OrderRepository orderRepository,
            final ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;

    }

    @Transactional
    public void execute(UUID id) {
        OrderModel order = orderRepository
                .findById(id)
                .orElseThrow(() -> new HttpNotFoundException(OrderExceptionMessageUtils.ORDER_NOT_FOUND));

        if(order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new HttpBadRequestException(OrderExceptionMessageUtils.ORDER_ALREADY_CANCELLED);
        }

        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
