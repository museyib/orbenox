package com.orbenox.erp.notificationservice.consumer;

public record EventMessage(
        Long eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        String aggregateVersion,
        String payload
) {
}
