package com.orbenox.erp.messaging.event;

public record DocumentEvent(
        Long id,
        String documentNo,
        String documentDate,
        String documentStatus,
        String approvalStatus,
        String description
) {
}
