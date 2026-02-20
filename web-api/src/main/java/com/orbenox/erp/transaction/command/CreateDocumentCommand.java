package com.orbenox.erp.transaction.command;

import java.time.LocalDate;
import java.util.List;

public record CreateDocumentCommand(
        LocalDate documentDate,
        Long typeId,
        String description,
        Long partnerId,
        String paymentMethod,
        Long priceListId,
        Long sourceWarehouseId,
        Long targetWarehouseId,
        List<ProductLineCommand> lines
) { }