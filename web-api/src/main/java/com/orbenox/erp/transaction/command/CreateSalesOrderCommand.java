package com.orbenox.erp.transaction.command;

import com.orbenox.erp.transaction.idempotency.Fingerprintable;

import java.time.LocalDate;
import java.util.List;

public record CreateSalesOrderCommand(
        LocalDate documentDate,
        String description,
        Long partnerId,
        String paymentMethod,
        Long priceListId,
        Long sourceWarehouseId,
        List<ProductLineCommand> lines
) implements DocumentCommand, Fingerprintable {

    @Override
    public Object getFingerprintFields() {
        String line = lines.stream()
                .map(productLineCommand -> productLineCommand.getFingerprintFields().toString())
                .reduce("", (a, b) -> a + b);
        return String.format("%s-%s-%s-%s-%s-%s-%s", documentDate, description, partnerId, paymentMethod, priceListId, sourceWarehouseId, line);
    }
}
