package com.orbenox.erp.common.event;

public record EventResponse(
        boolean success,
        String message
) {
}
