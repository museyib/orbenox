package com.orbenox.erp.transaction.command;

import com.orbenox.erp.transaction.idempotency.Fingerprintable;

import java.time.LocalDate;
import java.util.List;

public record CreateProductApproveCommand(
        LocalDate documentDate,
        String description,
        String paymentMethod,
        Long priceListId,
        Long targetWarehouseId,
        List<ProductLineCommand> lines
) implements DocumentCommand, Fingerprintable {
    @Override
    public Object getFingerprintFields() {
        String line = lines.stream()
                .map(productLineCommand -> productLineCommand.getFingerprintFields().toString())
                .reduce("", (a, b) -> a + b);
        return String.format("%s-%s-%s-%s-%s-%s", documentDate, description, paymentMethod, priceListId, targetWarehouseId, line);
    }
}
