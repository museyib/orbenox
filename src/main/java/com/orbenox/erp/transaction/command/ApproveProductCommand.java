package com.orbenox.erp.transaction.command;

import java.time.LocalDate;
import java.util.List;

public record ApproveProductCommand(
        String number,
        LocalDate date,
        Long typeId,
        String description,
        Long warehouseId,
        List<ProductLineCommand> lines
) {
}
