package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.transaction.entity.Document;

public class NoApprovalPolicy implements ApprovalPolicy {
    @Override
    public boolean requiresApproval(Document document) {
        return false;
    }
}
