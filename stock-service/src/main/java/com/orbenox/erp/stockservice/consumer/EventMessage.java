package com.orbenox.erp.stockservice.consumer;

public record EventMessage(
        Long eventId,
        String eventType,
        String aggregateType,
        Long aggregateId,
        String aggregateVersion,
        String payload
) {
}
