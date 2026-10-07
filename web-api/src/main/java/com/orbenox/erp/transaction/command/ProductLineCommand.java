package com.orbenox.erp.transaction.command;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductLineCommand(
        Long productId,
        Long unitId,
        @Positive BigDecimal quantity,
        @Positive BigDecimal unitPrice,
        @Positive BigDecimal discountRatio
) {
}
