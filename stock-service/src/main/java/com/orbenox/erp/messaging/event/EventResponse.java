package com.orbenox.erp.messaging.event;

public record EventResponse(
        boolean success,
        String message
) {
}
