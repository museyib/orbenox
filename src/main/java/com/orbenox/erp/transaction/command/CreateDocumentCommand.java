package com.orbenox.erp.transaction.command;

import java.time.LocalDate;

public record CreateDocumentCommand(
        String number,
        LocalDate date,
        Long typeId,
        String description,
        Long partnerId,
        String paymentMethod
) { }
