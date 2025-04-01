package com.emanueldev.sample_shop.domain.orders.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    private UUID productId;
    private String productName;
    private Long quantity;
    private BigDecimal unitPrice;
}
