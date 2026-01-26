package com.orbenox.erp.transaction.service;

import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;

public interface DocumentActionService {
    Document createDraft(CreateDocumentCommand command);

    void submit(Long documentId);

    void approve(Long documentId);

    void post(Long documentId);

    void reject(Long documentId);

    void close(Long documentId);

    void cancel(Long documentId);
}
