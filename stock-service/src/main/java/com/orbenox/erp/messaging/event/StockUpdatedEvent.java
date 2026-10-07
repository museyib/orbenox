package com.orbenox.erp.messaging.event;

import com.orbenox.erp.messaging.command.StockMovementCommand;

import java.util.List;

public record StockUpdatedEvent(
        boolean success,
        Long documentId,
        String typeCode,
        String message,
        List<StockMovementCommand.StockOperation> operations
) {
}
