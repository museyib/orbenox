package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.StockAffectDirection;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import com.orbenox.erp.transaction.service.StockService;
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
    private StockService stockService;

    @InjectMocks
    private DefaultDocumentPostPolicy policy;

    @Test
    void post_shouldInvokeAllAffectedContextServices() {
        Document document = documentWithType(true, StockAffectDirection.IN, true);

        policy.post(document);

        verify(accountingService).post(document);
        verify(stockService).post(document);
        verify(commercialService).post(document);
    }

    @Test
    void post_shouldSkipUnaffectedContextServices() {
        Document document = documentWithType(false, null, false);

        policy.post(document);

        verifyNoInteractions(accountingService, stockService, commercialService);
    }

    private Document documentWithType(boolean accountingAffected,
                                      StockAffectDirection stockAffectDirection,
                                      boolean commercialAffected) {
        TransactionType type = new TransactionType();
        type.setAccountingAffected(accountingAffected);
        type.setStockAffectDirection(stockAffectDirection);
        type.setCommercialAffected(commercialAffected);

        Document document = new Document();
        document.setType(type);
        return document;
    }
}
