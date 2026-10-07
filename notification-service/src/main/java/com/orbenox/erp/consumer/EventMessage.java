package com.orbenox.erp.consumer;

public record EventMessage(
        Long eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        String aggregateVersion,
        String payload
) {
}
