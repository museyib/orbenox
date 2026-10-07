package com.orbenox.erp.transaction.policy.approval;

import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.RootPolicy;

public interface ApprovalPolicy extends RootPolicy {
    boolean requiresApproval(Document document);
}