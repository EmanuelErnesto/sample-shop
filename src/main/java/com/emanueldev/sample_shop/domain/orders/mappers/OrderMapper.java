package com.emanueldev.sample_shop.domain.orders.mappers;


import com.emanueldev.sample_shop.domain.orders.dto.response.OrderItemDTO;
import com.emanueldev.sample_shop.domain.orders.dto.response.OrderResponseDTO;
import com.emanueldev.sample_shop.domain.orders.dto.response.PaginatedOrderResponseDTO;
import com.emanueldev.sample_shop.domain.payments.dtos.response.PaymentResponseDTO;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.models.OrderItem;
import com.emanueldev.sample_shop.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OrderMapper {

    public PaginatedOrderResponseDTO mappingFromOrderPageToPaginatedOrderDTO(Page<OrderModel> orderPage){
        List<OrderResponseDTO> orderResponseDTOList = orderPage
                .getContent()
                .stream()
                .map(this::mappingFromOrderToOrderResponseDTO)
                .toList();

        return PaginatedOrderResponseDTO
                .builder()
                .orders(orderResponseDTOList)
                .current_page(orderPage.getNumber())
                .total_items(orderPage.getTotalElements())
                .total_pages(orderPage.getTotalPages())
                .build();
    }

    public OrderResponseDTO mappingFromOrderToOrderResponseDTO(OrderModel order) {
        OrderResponseDTO response = OrderResponseDTO
                .builder()
                .id(order.getId())
                .totalValue(order.getTotalValue())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .items(this.mappingFromOrderItemListToOrderItemDTO(order.getOrderItems()))
                .build();


        Payment payment = order.getPayment();



        if (payment == null) {
            return response;
        }


        PaymentResponseDTO paymentResponseDTO =
                PaymentResponseDTO
                        .builder()
                        .id(payment.getId())
                        .amount(payment.getAmount())
                        .paymentDate(payment.getPaymentDate())
                        .paymentStatus(payment.getPaymentStatus())
                        .build();

        response.setPayment(paymentResponseDTO);

        return response;
    }

    private List<OrderItemDTO> mappingFromOrderItemListToOrderItemDTO(List<OrderItem> orderItemList) {
        return orderItemList.stream().map(orderItem -> {
             return OrderItemDTO
                     .builder()
                     .productId(orderItem.getProduct().getId())
                     .productName(orderItem.getProduct().getName())
                     .quantity(orderItem.getQuantity())
                     .unitPrice(orderItem.getUnitPrice())
                     .build();
        }).toList();
    }
}
