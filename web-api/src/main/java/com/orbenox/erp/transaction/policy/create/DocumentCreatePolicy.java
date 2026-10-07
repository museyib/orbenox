package com.orbenox.erp.transaction.policy.create;

import com.orbenox.erp.transaction.command.DocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.RootPolicy;

public interface DocumentCreatePolicy<T extends DocumentCommand> extends RootPolicy {
    void apply(Document document, T command);
}
