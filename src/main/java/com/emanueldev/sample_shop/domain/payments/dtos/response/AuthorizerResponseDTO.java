package com.emanueldev.sample_shop.domain.payments.dtos.response;


import com.emanueldev.sample_shop.domain.payments.enums.AuthorizationStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthorizerResponseDTO {

    private AuthorizationStatus status;
    private AuthorizerSuccessDTO data;
}
