package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.StockAffectDirection;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DefaultDocumentPostPolicyTest {

    @Mock
    private AccountingService accountingService;

    @Mock
    private CommercialService commercialService;

    @Mock
    private OutboxEventService outboxEventService;

    @InjectMocks
    private DefaultDocumentPostPolicy policy;

    @Test
    void post_shouldInvokeAllAffectedContextServices() {
        Document document = documentWithType(true, StockAffectDirection.IN, true);
        String eventType = "PRODUCT_APPROVE_POSTED";
        String aggregateType = "PRODUCT_APPROVE";

        policy.post(document);

        verify(accountingService).post(document);
        verify(commercialService).post(document);
        verify(outboxEventService).createOutboxEvent(document, eventType, aggregateType);
    }

    @Test
    void post_shouldSkipUnaffectedContextServices() {
        Document document = documentWithType(false, null, false);

        policy.post(document);

        verifyNoInteractions(accountingService, commercialService);
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
        document.setType(type);
        return document;
    }
}
