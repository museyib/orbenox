package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import com.orbenox.erp.transaction.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
@RequiredArgsConstructor
public class DefaultDocumentPostPolicy implements DocumentPostPolicy {
    private final AccountingService accountingService;
    private final CommercialService commercialService;
    private final StockService stockService;

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
            stockService.post(document);
        }

        if (document.getType().isCommercialAffected()) {
            commercialService.post(document);
        }
    }
}
