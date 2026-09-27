package com.orbenox.erp.eventpublisher.outbox;

public record EventMessage(
        Long eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        String aggregateVersion,
        String payload,
        String content
) {
}
