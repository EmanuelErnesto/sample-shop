package com.emanueldev.sample_shop.domain.payments.mappers;

import com.emanueldev.sample_shop.domain.payments.dtos.response.PaginatedPaymentResponseDTO;
import com.emanueldev.sample_shop.domain.payments.dtos.response.PaymentResponseDTO;
import com.emanueldev.sample_shop.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {

    public PaymentResponseDTO mappingFromPaymentEntityToPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO
                .builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }

    public PaginatedPaymentResponseDTO mappingFromPaymentPageToPaginatedPaymentDTO(Page<Payment> paymentPage) {
        List<PaymentResponseDTO> paymentResponseDTOList =
                paymentPage
                        .getContent()
                        .stream()
                        .map(this::mappingFromPaymentEntityToPaymentResponseDTO)
                        .toList();

        return PaginatedPaymentResponseDTO
                .builder()
                .payments(paymentResponseDTOList)
                .current_page(paymentPage.getNumber())
                .total_items(paymentPage.getTotalElements())
                .total_pages(paymentPage.getTotalPages())
                .build();
    }
}
