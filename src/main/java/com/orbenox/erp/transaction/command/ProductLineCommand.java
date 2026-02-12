package com.orbenox.erp.transaction.command;

import java.math.BigDecimal;

public record ProductLineCommand(
        Long productId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discountRatio
) {}
