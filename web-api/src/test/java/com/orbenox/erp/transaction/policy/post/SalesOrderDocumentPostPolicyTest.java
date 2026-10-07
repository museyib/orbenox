package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderDocumentPostPolicyTest {

    @Mock
    private DefaultDocumentPostPolicy defaultPolicy;

    @InjectMocks
    private SalesOrderDocumentPostPolicy policy;

    @Test
    void supports_shouldMatchSalesOrderCode() {
        TransactionType supported = new TransactionType();
        supported.setCode("SALES_ORDER");

        TransactionType unsupported = new TransactionType();
        unsupported.setCode("PRODUCT_APPROVE");

        org.assertj.core.api.Assertions.assertThat(policy.supports(supported)).isTrue();
        org.assertj.core.api.Assertions.assertThat(policy.supports(unsupported)).isFalse();
    }

    @Test
    void post_shouldDelegateWhenAllQuantitiesArePositive() {
        Document document = documentWithQuantities("2.5", "10");
        when(defaultPolicy.allQuantitiesPositive(document)).thenReturn(true);

        policy.post(document);

        verify(defaultPolicy).post(document);
    }

    private Document documentWithQuantities(String... quantities) {
        Document document = new Document();
        document.setProductLines(List.of(quantities).stream()
                .map(this::lineWithQuantity)
                .toList());
        return document;
    }

    private ProductLine lineWithQuantity(String quantity) {
        ProductLine line = new ProductLine();
        line.setQuantity(new BigDecimal(quantity));
        return line;
    }
}
