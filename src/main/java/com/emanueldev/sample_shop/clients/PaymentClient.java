package com.emanueldev.sample_shop.clients;

import com.emanueldev.sample_shop.domain.payments.dtos.response.AuthorizerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "paymentAuthorizer", url = "https://util.devi.tools/api/v2/authorize")
public interface PaymentClient {

    @GetMapping
    ResponseEntity<AuthorizerResponseDTO> validatePaymentAuthorization();
}
