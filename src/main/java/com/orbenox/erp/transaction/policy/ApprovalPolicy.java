package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.transaction.entity.Document;

public interface ApprovalPolicy {
    boolean requiresApproval(Document document);
}
