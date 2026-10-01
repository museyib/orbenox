package com.orbenox.erp.stockservice.consumer;

public record EventResponse(
        boolean success,
        String message
) {
}
