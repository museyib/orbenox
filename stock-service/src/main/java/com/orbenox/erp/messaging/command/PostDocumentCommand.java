package com.orbenox.erp.messaging.command;

public record PostDocumentCommand(
        Long id,
        String documentNo,
        String documentDate,
        String documentStatus,
        String approvalStatus,
        String description,
        String typeCode
) { }
