package com.orbenox.erp.transaction.command;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductLineCommand(
        Long productId,
        @Positive BigDecimal quantity,
        @Positive BigDecimal unitPrice,
        @Positive BigDecimal discountRatio
) {}
