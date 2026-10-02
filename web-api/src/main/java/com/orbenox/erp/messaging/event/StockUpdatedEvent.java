package com.orbenox.erp.messaging.event;

public record StockUpdatedEvent(
        boolean success,
        Long documentId,
        String typeCode,
        String message
) { }
