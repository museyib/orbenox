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

class SalesOrderActionServiceTest {
    private static final Long DOCUMENT_ID = 73L;

    private static Fixture fixture(DocumentStatus status, ApprovalStatus approvalStatus) {
        Document document = new Document();
        document.setDocumentStatus(status);
        document.setApprovalStatus(approvalStatus);
        TransactionType type = new TransactionType();
        type.setCode("SALES_ORDER");
        type.setName("Sales Order");
        document.setType(type);

        ApprovalPolicy approvalPolicy = mock(ApprovalPolicy.class);
        when(approvalPolicy.supports(type)).thenReturn(true);
        DocumentPostPolicy postPolicy = mock(DocumentPostPolicy.class);
        when(postPolicy.supports(type)).thenReturn(true);
        PolicyResolver<ApprovalPolicy> approvalResolver = new PolicyResolver<>(List.of(approvalPolicy));
        PolicyResolver<DocumentPostPolicy> postResolver = new PolicyResolver<>(List.of(postPolicy));
        DocumentResolver documentResolver = mock(DocumentResolver.class);
        when(documentResolver.resolve(DOCUMENT_ID, "SALES_ORDER")).thenReturn(document);
        LocalizationService localization = mock(LocalizationService.class);

        SalesOrderActionService service = new SalesOrderActionService(
                mock(DocumentService.class),
                approvalResolver,
                postResolver,
                localization,
                documentResolver,
                mock(OutboxEventService.class));
        return new Fixture(service, document, approvalPolicy, postPolicy, documentResolver, localization);
    }

    @Test
    void submit_shouldSetPendingApprovalWhenDraftRequiresApproval() {
        Fixture fixture = fixture(DocumentStatus.DRAFT, ApprovalStatus.AUTO_APPROVED);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(true);

        fixture.service.submit(DOCUMENT_ID);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
        verify(fixture.documentResolver).resolve(DOCUMENT_ID, "SALES_ORDER");
    }

    @Test
    void submit_shouldAutoApproveDraftWhenApprovalIsNotRequired() {
        Fixture fixture = fixture(DocumentStatus.DRAFT, ApprovalStatus.PENDING);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(false);

        fixture.service.submit(DOCUMENT_ID);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.AUTO_APPROVED);
        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
    }

    @Test
    void submit_shouldRejectNonDraftWithoutChangingDocumentState() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.PENDING);

        assertThatThrownBy(() -> fixture.service.submit(DOCUMENT_ID))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
        verify(fixture.localization).msg("error.document.onlyDraftCanBeSubmitted");
        verify(fixture.approvalPolicy, never()).requiresApproval(fixture.document);
    }

    @Test
    void approve_shouldApprovePendingDocumentWhenApprovalIsRequired() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.PENDING);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(true);

        fixture.service.approve(DOCUMENT_ID);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
    }

    @Test
    void approve_shouldRejectWhenApprovalIsNotRequired() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.PENDING);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(false);

        assertThatThrownBy(() -> fixture.service.approve(DOCUMENT_ID))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
        verify(fixture.localization).msg("error.document.approvalNotRequired");
        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
    }

    @Test
    void approve_shouldRejectWhenApprovalIsRequiredButDocumentIsNotPending() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.AUTO_APPROVED);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(true);

        assertThatThrownBy(() -> fixture.service.approve(DOCUMENT_ID))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.AUTO_APPROVED);
        verify(fixture.localization).msg("error.document.notPendingApproval");
        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
    }

    @Test
    void post_shouldCallPostPolicyAndSetPostingStatusWhenApprovalIsRequiredAndGranted() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.APPROVED);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(true);

        fixture.service.post(DOCUMENT_ID);

        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
        verify(fixture.postPolicy).post(fixture.document);
        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.POSTING);
        assertThat(fixture.document.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }

    @Test
    void post_shouldPostWithoutApprovalWhenApprovalIsNotRequired() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.AUTO_APPROVED);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(false);

        fixture.service.post(DOCUMENT_ID);

        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
        verify(fixture.postPolicy).post(fixture.document);
        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.POSTING);
    }

    @Test
    void post_shouldRejectAlreadyPostedDocument() {
        Fixture fixture = fixture(DocumentStatus.POSTED, ApprovalStatus.APPROVED);

        assertThatThrownBy(() -> fixture.service.post(DOCUMENT_ID))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.POSTED);
        verify(fixture.localization).msg("error.document.alreadyPosted");
        verifyNoInteractions(fixture.postPolicy);
        verify(fixture.approvalPolicy, never()).requiresApproval(fixture.document);
    }

    @Test
    void post_shouldRejectDocumentThatIsNotInProgress() {
        Fixture fixture = fixture(DocumentStatus.DRAFT, ApprovalStatus.APPROVED);

        assertThatThrownBy(() -> fixture.service.post(DOCUMENT_ID))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.DRAFT);
        verify(fixture.localization).msg("error.document.onlySubmittedCanBePosted");
        verifyNoInteractions(fixture.postPolicy);
        verify(fixture.approvalPolicy, never()).requiresApproval(fixture.document);
    }

    @Test
    void post_shouldRejectUnapprovedDocumentWhenApprovalIsRequired() {
        Fixture fixture = fixture(DocumentStatus.IN_PROGRESS, ApprovalStatus.PENDING);
        when(fixture.approvalPolicy.requiresApproval(fixture.document)).thenReturn(true);

        assertThatThrownBy(() -> fixture.service.post(DOCUMENT_ID))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(fixture.document.getDocumentStatus()).isEqualTo(DocumentStatus.IN_PROGRESS);
        verify(fixture.localization).msg("error.document.notApproved");
        verify(fixture.approvalPolicy).requiresApproval(fixture.document);
        verifyNoInteractions(fixture.postPolicy);
    }

    private record Fixture(
            SalesOrderActionService service,
            Document document,
            ApprovalPolicy approvalPolicy,
            DocumentPostPolicy postPolicy,
            DocumentResolver documentResolver,
            LocalizationService localization) {
    }
}
