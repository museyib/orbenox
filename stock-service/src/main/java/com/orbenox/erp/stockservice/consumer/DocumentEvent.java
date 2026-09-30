package com.orbenox.erp.stockservice.consumer;

public record DocumentEvent(
        Long id,
        String documentNo,
        String documentDate,
        String documentStatus,
        String approvalStatus,
        String description
) {
}
