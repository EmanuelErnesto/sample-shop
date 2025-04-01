package com.emanueldev.sample_shop.services.orders;

import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import com.emanueldev.sample_shop.exceptions.HttpNotFoundException;
import com.emanueldev.sample_shop.domain.orders.dto.request.OrderRequestDTO;
import com.emanueldev.sample_shop.models.*;
import com.emanueldev.sample_shop.repositories.OrderRepository;
import com.emanueldev.sample_shop.repositories.ProductRepository;
import com.emanueldev.sample_shop.utils.OrderExceptionMessageUtils;
import com.emanueldev.sample_shop.utils.ProductExceptionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreateOrderUseCase {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CreateOrderUseCase(final ProductRepository productRepository, final OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderModel execute(OrderRequestDTO orderRequestDTO) {
        List<OrderItem> orderItemList = orderRequestDTO
                .getItems()
                .stream()
                .map(orderItemDTO -> {
                    Product productExists = this.getProductById(orderItemDTO.getProductId());
                    Long productStockQuantity = productExists.getStockQuantity();
                    Long orderItemDTOQuantity = orderItemDTO.getQuantity();

                    if(productStockQuantity == 0) {
                        throw new HttpBadRequestException(OrderExceptionMessageUtils.PRODUCT_OUT_OF_STOCK);
                    }

                    if(orderItemDTOQuantity > productStockQuantity) {
                        throw new HttpBadRequestException(OrderExceptionMessageUtils.PRODUCT_DONT_HAVE_SUFFICIENT_STOCK);
                    }

                    productExists.setStockQuantity(productStockQuantity - orderItemDTOQuantity);

                    productRepository.save(productExists);

                    return OrderItem
                            .builder()
                            .product(productExists)
                            .quantity(orderItemDTO.getQuantity())
                            .unitPrice(productExists.getPrice())
                            .build();
                })
                .toList();

        BigDecimal totalValue = orderItemList.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderModel order = OrderModel
                .builder()
                .totalValue(totalValue)
                .orderStatus(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        orderItemList.forEach(item -> {
            item.setOrder(order);
            order.getOrderItems().add(item);
        });

        return orderRepository.save(order);
    }

    private Product getProductById (String productId) {
        UUID id = UUID.fromString(productId);

        return productRepository.findById(id).orElseThrow(() ->
                new HttpNotFoundException(ProductExceptionMessageUtils.PRODUCT_NOT_FOUND));
    }

}
