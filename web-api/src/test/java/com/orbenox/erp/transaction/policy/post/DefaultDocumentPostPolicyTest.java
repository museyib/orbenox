package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.enums.StockAffectDirection;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import com.orbenox.erp.transaction.service.StockPostingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DefaultDocumentPostPolicyTest {

    @Mock
    private AccountingService accountingService;

    @Mock
    private CommercialService commercialService;
    @Mock
    private StockPostingService stockPostingService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DefaultDocumentPostPolicy policy;

    @Test
    void post_shouldInvokeAllAffectedContextServices() {
        Document document = documentWithType(true, StockAffectDirection.IN, true);

        policy.post(document);

        verify(accountingService).post(document);
        verify(commercialService).post(document);
        verify(stockPostingService).post(document);
    }

    @Test
    void post_shouldSkipUnaffectedContextServices() {
        Document document = documentWithType(false, null, false);

        policy.post(document);

        verifyNoInteractions(accountingService, commercialService, rabbitTemplate);
    }

    private Document documentWithType(boolean accountingAffected,
                                      StockAffectDirection stockAffectDirection,
                                      boolean commercialAffected) {
        TransactionType type = new TransactionType();
        type.setAccountingAffected(accountingAffected);
        type.setStockAffectDirection(stockAffectDirection);
        type.setCommercialAffected(commercialAffected);
        type.setCode("PRODUCT_APPROVE");

        Document document = new Document();
        document.setId(42L);
        document.setDocumentNo("DOC-42");
        document.setDocumentDate(LocalDate.of(2026, 1, 2));
        document.setDocumentStatus(DocumentStatus.IN_PROGRESS);
        document.setApprovalStatus(ApprovalStatus.APPROVED);
        document.setDescription("Test document");
        document.setType(type);
        return document;
    }
}
