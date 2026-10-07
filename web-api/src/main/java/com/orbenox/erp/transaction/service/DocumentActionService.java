package com.orbenox.erp.transaction.service;

import com.orbenox.erp.transaction.command.DocumentCommand;
import com.orbenox.erp.transaction.entity.Document;

public interface DocumentActionService<T extends DocumentCommand> {
    Document createDraft(T command);

    void submit(Long documentId);

    void approve(Long documentId);

    void post(Long documentId);

    void reject(Long documentId);

    void close(Long documentId);

    void cancel(Long documentId);
}
