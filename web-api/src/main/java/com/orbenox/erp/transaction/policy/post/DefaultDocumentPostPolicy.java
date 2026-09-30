package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order
@RequiredArgsConstructor
public class DefaultDocumentPostPolicy implements DocumentPostPolicy {
    private final AccountingService accountingService;
    private final CommercialService commercialService;
    private final OutboxEventService outboxEventService;

    @Override
    public boolean supports(TransactionType type) {
        return true;
    }

    @Override
    public void post(Document document) {
        if (document.getType().isAccountingAffected()) {
            accountingService.post(document);
        }

        if (document.getType().isStockAffected()) {
            String aggregateType = document.getType().getCode();
            String eventType = aggregateType + "_POSTED";
            outboxEventService.createOutboxEvent(document, eventType, aggregateType);
        }

        if (document.getType().isCommercialAffected()) {
            commercialService.post(document);
        }
    }

    protected boolean allQuantitiesPositive(Document document) {
        return document.getProductLines()
                .stream()
                .allMatch(line -> line.getQuantity().compareTo(BigDecimal.ZERO) > 0);
    }
}
