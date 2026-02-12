package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;

public interface ApprovalPolicy {
    boolean supports(TransactionType type);
    boolean requiresApproval(Document document);
}
