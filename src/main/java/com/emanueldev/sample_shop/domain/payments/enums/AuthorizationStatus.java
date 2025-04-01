package com.emanueldev.sample_shop.domain.payments.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorizationStatus {
    SUCCESS("success"),
    FAIL("fail");

    private final String status;

    AuthorizationStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}