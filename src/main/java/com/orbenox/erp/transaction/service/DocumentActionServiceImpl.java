package com.orbenox.erp.transaction.service;

import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.SalesOrderApprovalPolicy;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentActionServiceImpl implements DocumentActionService {

    private final DocumentRepository documentRepo;
    private final DocumentService documentService;
    private final AccountingService accountingService;
    private final CommercialService commercialService;
    private final StockService stockService;

    private final SalesOrderApprovalPolicy approvalPolicy;

    @Override
    public Document createDraft(CreateDocumentCommand command) {
        return documentService.createDocument(command);
    }

    @Override
    public void submit(Long documentId) {

    }

    @Override
    public void approve(Long documentId) {

    }

    @Override
    public void post(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.isPosted())
            throw new IllegalStateException("Document is already posted");

        if (doc.getDocumentStatus() != DocumentStatus.DRAFT)
            throw new IllegalStateException("Only draft can be posted");

        if (approvalPolicy.requiresApproval(doc) &&
                doc.getApprovalStatus() != ApprovalStatus.APPROVED)
            throw new IllegalStateException("Document not approved");

        if (doc.getType().isAffectsAccount())
            accountingService.post(doc);

        if (doc.getType().isAffectsStock())
            stockService.post(doc);

        if (doc.getType().isAffectsCommercial())
            commercialService.post(doc);

        doc.setDocumentStatus(DocumentStatus.POSTED);
    }

    @Override
    public void reject(Long documentId) {

    }

    @Override
    public void close(Long documentId) {

    }

    @Override
    public void cancel(Long documentId) {

    }
}
