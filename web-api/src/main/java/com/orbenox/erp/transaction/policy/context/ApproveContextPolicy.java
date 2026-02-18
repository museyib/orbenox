package com.orbenox.erp.transaction.policy.context;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.StockContext;
import org.springframework.stereotype.Component;

@Component
public class ApproveContextPolicy implements ContextPolicy {
    @Override
    public void validate(Document document) {
        if (document == null)
            throw new BusinessRuleException("Document is null");
        StockContext stockContext = document.getStockContext();
        if (stockContext == null)
            throw new BusinessRuleException("StockContext is null");
        if (stockContext.getTargetWarehouse() == null) {
            throw new BusinessRuleException("Target Warehouse is not defined");
        } else if (stockContext.getSourceWarehouse() != null) {
            throw new BusinessRuleException("Source Warehouse cannot be applied for this document");
        }
    }

    @Override
    public boolean supports(TransactionType type) {
        return "APPROVE".equals(type.getCode());
    }
}