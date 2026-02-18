package com.orbenox.erp.transaction.policy.context;

import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.RootPolicy;

public interface ContextPolicy extends RootPolicy {
    void validate(Document document);
}