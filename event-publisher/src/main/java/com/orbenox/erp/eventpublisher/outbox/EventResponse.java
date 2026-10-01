package com.orbenox.erp.eventpublisher.outbox;

public record EventResponse(
        boolean success,
        String message
) {
}
