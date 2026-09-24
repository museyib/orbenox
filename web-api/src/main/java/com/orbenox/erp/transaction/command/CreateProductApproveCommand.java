package com.orbenox.erp.transaction.command;

import java.time.LocalDate;
import java.util.List;

public record CreateProductApproveCommand(
        LocalDate documentDate,
        String description,
        String paymentMethod,
        Long priceListId,
        Long targetWarehouseId,
        List<ProductLineCommand> lines
) implements DocumentCommand { }
