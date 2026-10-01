package com.orbenox.erp.common.event;

public record DocumentEvent(
        Long id,
        String documentNo,
        String documentDate,
        String documentStatus,
        String approvalStatus,
        String description
) {
}
