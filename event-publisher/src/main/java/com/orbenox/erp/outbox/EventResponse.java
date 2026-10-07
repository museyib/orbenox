package com.orbenox.erp.outbox;

public record EventResponse(
        boolean success,
        String message
) {
}
