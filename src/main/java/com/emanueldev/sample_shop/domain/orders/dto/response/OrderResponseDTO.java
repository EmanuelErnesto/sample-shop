package com.emanueldev.sample_shop.domain.orders.dto.response;

import com.emanueldev.sample_shop.domain.payments.dtos.response.PaymentResponseDTO;
import com.emanueldev.sample_shop.models.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
    private UUID id;
    private BigDecimal totalValue;
    private OrderStatus orderStatus;
    private PaymentResponseDTO payment;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
}
