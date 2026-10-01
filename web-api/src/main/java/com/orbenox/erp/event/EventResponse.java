package com.orbenox.erp.event;

public record EventResponse(
        boolean success,
        String message
) {
}
