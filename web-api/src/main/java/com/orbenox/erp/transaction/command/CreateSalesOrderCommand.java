package com.orbenox.erp.transaction.command;

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
) implements DocumentCommand { }
