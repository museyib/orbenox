package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class DefaultApprovalPolicy implements ApprovalPolicy {
    @Override
    public boolean supports(TransactionType type) {
        return true;
    }

    @Override
    public boolean requiresApproval(Document document) {
        return document.getType().isApprovalRequired();
    }
}
