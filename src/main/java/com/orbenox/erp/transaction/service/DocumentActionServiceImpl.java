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
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getDocumentStatus() != DocumentStatus.DRAFT)
            throw new IllegalStateException("Only draft can be submitted");

        doc.setDocumentStatus(DocumentStatus.IN_PROGRESS);

        if (approvalPolicy.requiresApproval(doc))
            doc.setApprovalStatus(ApprovalStatus.PENDING);
        else
            doc.setApprovalStatus(ApprovalStatus.AUTO_APPROVED);
    }

    @Override
    public void approve(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (!approvalPolicy.requiresApproval(doc))
            throw new IllegalStateException("Approval not required");

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING)
            throw new IllegalStateException("Document not pending approval");

        doc.setApprovalStatus(ApprovalStatus.APPROVED);
    }

    @Override
    public void post(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.isPosted())
            throw new IllegalStateException("Document is already posted");

        if (doc.getDocumentStatus() != DocumentStatus.IN_PROGRESS)
            throw new IllegalStateException("Only submitted documents can be posted");

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
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING)
            throw new IllegalStateException("Only pending documents can be rejected");

        doc.setApprovalStatus(ApprovalStatus.REJECTED);
        doc.setDocumentStatus(DocumentStatus.DRAFT);
    }

    @Override
    public void close(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getDocumentStatus() != DocumentStatus.POSTED)
            throw new IllegalStateException("Only posted documents can be closed");

        doc.setDocumentStatus(DocumentStatus.CLOSED);
    }

    @Override
    public void cancel(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getDocumentStatus() == DocumentStatus.POSTED)
            throw new IllegalStateException("Posted documents can not be cancelled");

        doc.setDocumentStatus(DocumentStatus.CANCELLED);
    }
}
