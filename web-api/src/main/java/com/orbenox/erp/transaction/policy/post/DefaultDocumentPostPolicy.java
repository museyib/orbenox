package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import com.orbenox.erp.transaction.service.StockPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order
@Slf4j
@RequiredArgsConstructor
public class DefaultDocumentPostPolicy implements DocumentPostPolicy {
    private final AccountingService accountingService;
    private final CommercialService commercialService;
    private final StockPostingService stockPostingService;

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
            stockPostingService.post(document);
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
