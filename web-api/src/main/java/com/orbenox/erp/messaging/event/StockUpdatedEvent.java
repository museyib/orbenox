package com.orbenox.erp.messaging.event;

import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;

import java.util.List;

public record StockUpdatedEvent(
        Boolean success,
        Long documentId,
        String typeCode,
        String message,
        List<StockOperation> operations
) {
}
