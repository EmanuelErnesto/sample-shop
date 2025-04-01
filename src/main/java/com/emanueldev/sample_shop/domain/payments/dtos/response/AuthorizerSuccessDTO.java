package com.emanueldev.sample_shop.domain.payments.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizerSuccessDTO {

    @JsonProperty("authorization")
    private boolean isAuthorized;
}
