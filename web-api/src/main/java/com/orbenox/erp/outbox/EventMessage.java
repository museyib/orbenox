package com.orbenox.erp.outbox;

public record EventMessage(
        Long eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        String aggregateVersion,
        String payload
) {
}
