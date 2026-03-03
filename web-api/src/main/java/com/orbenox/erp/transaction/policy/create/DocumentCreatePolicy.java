package com.orbenox.erp.transaction.policy.create;

import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.RootPolicy;

public interface DocumentCreatePolicy extends RootPolicy {
    void apply(Document document, CreateDocumentCommand command);
}
