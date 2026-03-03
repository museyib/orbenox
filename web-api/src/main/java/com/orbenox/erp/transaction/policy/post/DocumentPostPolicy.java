package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.RootPolicy;

public interface DocumentPostPolicy extends RootPolicy {
    void post(Document document);
}
