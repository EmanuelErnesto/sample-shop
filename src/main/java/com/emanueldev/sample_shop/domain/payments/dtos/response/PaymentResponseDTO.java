package com.emanueldev.sample_shop.domain.payments.dtos.response;

import com.emanueldev.sample_shop.utils.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PaymentResponseDTO {

    private UUID id;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;
}
