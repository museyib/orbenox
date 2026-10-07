package com.orbenox.erp.messaging.command;

import java.math.BigDecimal;
import java.util.List;

public record StockMovementCommand(Long documentId,
                                   String typeCode,
                                   List<StockOperation> operations) {
    public record StockOperation(Long lineId, Long productId, Long warehouseId, BigDecimal quantity, int direction) {
    }
}
