package com.orbenox.erp.transaction.command;

import com.orbenox.erp.transaction.idempotency.Fingerprintable;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductLineCommand (
        Long productId,
        @Positive BigDecimal quantity,
        @Positive BigDecimal unitPrice,
        @Positive BigDecimal discountRatio
) implements Fingerprintable {
    @Override
    public Object getFingerprintFields() {
        return String.format("%s-%s-%s-%s", productId, quantity, unitPrice, discountRatio);
    }
}
