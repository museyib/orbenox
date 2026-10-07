package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.policy.approval.ApprovalPolicy;
import com.orbenox.erp.transaction.policy.post.DocumentPostPolicy;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductApproveActionServiceTest {

    private static ProductApproveActionService service(Document document,
                                                       ApprovalPolicy approval,
                                                       DocumentPostPolicy posting) {
        DocumentService documentService = mock(DocumentService.class);
        when(approval.supports(any())).thenReturn(true);
        when(posting.supports(any())).thenReturn(true);
        PolicyResolver<ApprovalPolicy> approvalResolver = new PolicyResolver<>(List.of(approval));
        PolicyResolver<DocumentPostPolicy> postResolver = new PolicyResolver<>(List.of(posting));
        LocalizationService localization = mock(LocalizationService.class);
        DocumentResolver documentResolver = mock(DocumentResolver.class);
        when(documentResolver.resolve(7L, "PRODUCT_APPROVE")).thenReturn(document);
        return new ProductApproveActionService(documentService, approvalResolver, postResolver,
                localization, documentResolver, mock(OutboxEventService.class));
    }

    private static Document document(DocumentStatus status, ApprovalStatus approvalStatus) {
        TransactionType type = new TransactionType();
        type.setCode("PRODUCT_APPROVE");
        Document document = new Document();
        document.setType(type);
        document.setDocumentStatus(status);
        document.setApprovalStatus(approvalStatus);
        return document;
    }

    @Test
    void submit_shouldMoveDraftToProgressAndSetApprovalStatusWhenApprovalRequired() {
        Document document = document(DocumentStatus.DRAFT, ApprovalStatus.AUTO_APPROVED);
        ApprovalPolicy policy = mock(ApprovalPolicy.class);
        when(policy.requiresApproval(document)).thenReturn(true);
        ProductApproveActionService service = service(document, policy, mock(DocumentPostPolicy.class));

        service.submit(7L);

        assertThat(document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(document.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
    }

    @Test
    void submit_shouldAutoApproveWhenPolicyDoesNotRequireApproval() {
        Document document = document(DocumentStatus.DRAFT, ApprovalStatus.PENDING);
        ApprovalPolicy policy = mock(ApprovalPolicy.class);
        when(policy.requiresApproval(document)).thenReturn(false);
        ProductApproveActionService service = service(document, policy, mock(DocumentPostPolicy.class));

        service.submit(7L);

        assertThat(document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(document.getApprovalStatus()).isEqualTo(ApprovalStatus.AUTO_APPROVED);
    }

    @Test
    void submit_shouldRejectDocumentsThatAreNotDrafts() {
        Document document = document(DocumentStatus.IN_PROGRESS, ApprovalStatus.PENDING);
        ProductApproveActionService service = service(document, mock(ApprovalPolicy.class),
                mock(DocumentPostPolicy.class));

        assertThatThrownBy(() -> service.submit(7L)).isInstanceOf(BusinessRuleException.class);
        assertThat(document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
    }

    @Test
    void post_shouldDelegatePostingAndMarkDocumentAsPostingWhenApproved() {
        Document document = document(DocumentStatus.IN_PROGRESS, ApprovalStatus.APPROVED);
        ApprovalPolicy approval = mock(ApprovalPolicy.class);
        when(approval.requiresApproval(document)).thenReturn(true);
        DocumentPostPolicy posting = mock(DocumentPostPolicy.class);
        ProductApproveActionService service = service(document, approval, posting);

        service.post(7L);

        verify(posting).post(document);
        assertThat(document.getDocumentStatus()).isEqualTo(DocumentStatus.POSTING);
    }

    @Test
    void post_shouldRejectUnapprovedDocumentWhenApprovalIsRequired() {
        Document document = document(DocumentStatus.IN_PROGRESS, ApprovalStatus.PENDING);
        ApprovalPolicy approval = mock(ApprovalPolicy.class);
        when(approval.requiresApproval(document)).thenReturn(true);
        DocumentPostPolicy posting = mock(DocumentPostPolicy.class);

        assertThatThrownBy(() -> service(document, approval, posting).post(7L))
                .isInstanceOf(BusinessRuleException.class);
        verifyNoInteractions(posting);
        assertThat(document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
    }
}
