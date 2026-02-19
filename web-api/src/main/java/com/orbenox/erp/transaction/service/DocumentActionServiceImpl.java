package com.orbenox.erp.transaction.service;

import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.approval.ApprovalPolicy;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentActionServiceImpl implements DocumentActionService {

    private final DocumentRepository documentRepo;
    private final DocumentService documentService;
    private final AccountingService accountingService;
    private final CommercialService commercialService;
    private final StockService stockService;
    private final PolicyResolver<ApprovalPolicy> approvalPolicyResolver;
    private final LocalizationService i18n;

    @Override
    public Document createDraft(CreateDocumentCommand command) {
        return documentService.createDocument(command);
    }

    @Override
    @Transactional
    public void submit(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();
        ApprovalPolicy approvalPolicy = approvalPolicyResolver.resolve(doc.getType());

        if (doc.getDocumentStatus() != DocumentStatus.DRAFT)
            throw new BusinessRuleException(i18n.msg("error.document.onlyDraftCanBeSubmitted"));

        doc.setDocumentStatus(DocumentStatus.IN_PROGRESS);

        if (approvalPolicy.requiresApproval(doc))
            doc.setApprovalStatus(ApprovalStatus.PENDING);
        else
            doc.setApprovalStatus(ApprovalStatus.AUTO_APPROVED);
    }

    @Override
    @Transactional
    public void approve(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();
        ApprovalPolicy approvalPolicy = approvalPolicyResolver.resolve(doc.getType());

        if (!approvalPolicy.requiresApproval(doc))
            throw new BusinessRuleException(i18n.msg("error.document.approvalNotRequired"));

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING)
            throw new BusinessRuleException(i18n.msg("error.document.notPendingApproval"));

        doc.setApprovalStatus(ApprovalStatus.APPROVED);
    }

    @Override
    @Transactional
    public void post(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();
        ApprovalPolicy approvalPolicy = approvalPolicyResolver.resolve(doc.getType());

        if (doc.isPosted())
            throw new BusinessRuleException(i18n.msg("error.document.alreadyPosted"));

        if (doc.getDocumentStatus() != DocumentStatus.IN_PROGRESS)
            throw new BusinessRuleException(i18n.msg("error.document.onlySubmittedCanBePosted"));

        if (approvalPolicy.requiresApproval(doc) &&
                doc.getApprovalStatus() != ApprovalStatus.APPROVED)
            throw new BusinessRuleException(i18n.msg("error.document.notApproved"));

        if (doc.getType().isAccountingAffected())
            accountingService.post(doc);

        if (doc.getType().isStockAffected())
            stockService.post(doc);

        if (doc.getType().isCommercialAffected())
            commercialService.post(doc);

        doc.setDocumentStatus(DocumentStatus.POSTED);
    }

    @Override
    @Transactional
    public void reject(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING)
            throw new BusinessRuleException(i18n.msg("error.document.onlyPendingCanBeRejected"));

        doc.setApprovalStatus(ApprovalStatus.REJECTED);
        doc.setDocumentStatus(DocumentStatus.DRAFT);
    }

    @Override
    @Transactional
    public void close(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getDocumentStatus() != DocumentStatus.POSTED)
            throw new BusinessRuleException(i18n.msg("error.document.onlyPostedCanBeClosed"));

        doc.setDocumentStatus(DocumentStatus.CLOSED);
    }

    @Override
    @Transactional
    public void cancel(Long documentId) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (doc.getDocumentStatus() == DocumentStatus.POSTED)
            throw new BusinessRuleException(i18n.msg("error.document.postedCannotBeCancelled"));

        doc.setDocumentStatus(DocumentStatus.CANCELLED);
    }
}
