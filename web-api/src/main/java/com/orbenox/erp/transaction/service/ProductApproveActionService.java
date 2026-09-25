package com.orbenox.erp.transaction.service;

import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateProductApproveCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.approval.ApprovalPolicy;
import com.orbenox.erp.transaction.policy.post.DocumentPostPolicy;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import static com.orbenox.erp.config.CacheConfig.CacheNames.PRODUCT_WAREHOUSES;

@Service
@RequiredArgsConstructor
public class ProductApproveActionService implements DocumentActionService<CreateProductApproveCommand> {
    private static final String TRANSACTION_TYPE = "PRODUCT_APPROVE";

    private final DocumentService documentService;
    private final PolicyResolver<ApprovalPolicy> approvalPolicyResolver;
    private final PolicyResolver<DocumentPostPolicy> documentPostPolicyResolver;
    private final LocalizationService i18n;
    private final DocumentResolver documentResolver;

    @Override
    public Document createDraft(CreateProductApproveCommand command) {
        return documentService.createProductApprove(command);
    }

    @Override
    @Transactional
    public void submit(Long documentId) {
        Document doc = documentResolver.resolve(documentId, TRANSACTION_TYPE);
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
        Document doc = documentResolver.resolve(documentId, TRANSACTION_TYPE);
        ApprovalPolicy approvalPolicy = approvalPolicyResolver.resolve(doc.getType());

        if (!approvalPolicy.requiresApproval(doc))
            throw new BusinessRuleException(i18n.msg("error.document.approvalNotRequired"));

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING)
            throw new BusinessRuleException(i18n.msg("error.document.notPendingApproval"));

        doc.setApprovalStatus(ApprovalStatus.APPROVED);
    }

    @Override
    @Transactional
    @CacheEvict(value = PRODUCT_WAREHOUSES, allEntries = true)
    public void post(Long documentId) {
        Document doc = documentResolver.resolve(documentId, TRANSACTION_TYPE);
        ApprovalPolicy approvalPolicy = approvalPolicyResolver.resolve(doc.getType());

        if (doc.isPosted())
            throw new BusinessRuleException(i18n.msg("error.document.alreadyPosted"));

        if (doc.getDocumentStatus() != DocumentStatus.IN_PROGRESS)
            throw new BusinessRuleException(i18n.msg("error.document.onlySubmittedCanBePosted"));

        if (approvalPolicy.requiresApproval(doc) &&
                doc.getApprovalStatus() != ApprovalStatus.APPROVED)
            throw new BusinessRuleException(i18n.msg("error.document.notApproved"));

        documentPostPolicyResolver.resolve(doc.getType()).post(doc);

        doc.setDocumentStatus(DocumentStatus.POSTED);
    }

    @Override
    @Transactional
    public void reject(Long documentId) {
        Document doc = documentResolver.resolve(documentId, TRANSACTION_TYPE);

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING)
            throw new BusinessRuleException(i18n.msg("error.document.onlyPendingCanBeRejected"));

        doc.setApprovalStatus(ApprovalStatus.REJECTED);
        doc.setDocumentStatus(DocumentStatus.DRAFT);
    }

    @Override
    @Transactional
    public void close(Long documentId) {
        Document doc = documentResolver.resolve(documentId, TRANSACTION_TYPE);

        if (doc.getDocumentStatus() != DocumentStatus.POSTED)
            throw new BusinessRuleException(i18n.msg("error.document.onlyPostedCanBeClosed"));

        doc.setDocumentStatus(DocumentStatus.CLOSED);
    }

    @Override
    @Transactional
    public void cancel(Long documentId) {
        Document doc = documentResolver.resolve(documentId, TRANSACTION_TYPE);

        if (doc.getDocumentStatus() == DocumentStatus.POSTED)
            throw new BusinessRuleException(i18n.msg("error.document.postedCannotBeCancelled"));

        doc.setDocumentStatus(DocumentStatus.CANCELLED);
    }
}
