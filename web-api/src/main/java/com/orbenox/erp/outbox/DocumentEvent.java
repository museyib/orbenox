package com.orbenox.erp.outbox;

public record DocumentEvent(
        Long id,
        String documentNo,
        String documentDate,
        String documentStatus,
        String approvalStatus,
        String description
) {
}
