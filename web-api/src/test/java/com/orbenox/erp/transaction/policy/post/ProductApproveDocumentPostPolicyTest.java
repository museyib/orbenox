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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApproveDocumentPostPolicyTest {

    @Mock
    private DefaultDocumentPostPolicy defaultPolicy;

    @InjectMocks
    private ProductApproveDocumentPostPolicy policy;

    @Test
    void supports_shouldMatchProductApproveCode() {
        TransactionType supported = new TransactionType();
        supported.setCode("PRODUCT_APPROVE");

        TransactionType unsupported = new TransactionType();
        unsupported.setCode("SALES_ORDER");

        assertThat(policy.supports(supported)).isTrue();
        assertThat(policy.supports(unsupported)).isFalse();
    }

    @Test
    void post_shouldDelegateWhenAllQuantitiesArePositive() {
        Document document = documentWithQuantities("1", "3");
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
