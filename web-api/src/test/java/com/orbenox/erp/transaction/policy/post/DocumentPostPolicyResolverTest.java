package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DocumentPostPolicyResolverTest {

    @Test
    void resolvesSalesOrderPolicy() {
        DefaultDocumentPostPolicy defaultPolicy = mock(DefaultDocumentPostPolicy.class);
        PolicyResolver<DocumentPostPolicy> resolver = new PolicyResolver<>(List.of(
                new SalesOrderDocumentPostPolicy(defaultPolicy),
                new ProductApproveDocumentPostPolicy(defaultPolicy),
                defaultPolicy
        ));

        TransactionType type = new TransactionType();
        type.setCode("SALES_ORDER");

        DocumentPostPolicy resolved = resolver.resolve(type);
        assertInstanceOf(SalesOrderDocumentPostPolicy.class, resolved);
    }

    @Test
    void resolvesProductApprovePolicy() {
        DefaultDocumentPostPolicy defaultPolicy = mock(DefaultDocumentPostPolicy.class);
        PolicyResolver<DocumentPostPolicy> resolver = new PolicyResolver<>(List.of(
                new SalesOrderDocumentPostPolicy(defaultPolicy),
                new ProductApproveDocumentPostPolicy(defaultPolicy),
                defaultPolicy
        ));

        TransactionType type = new TransactionType();
        type.setCode("PRODUCT_APPROVE");

        DocumentPostPolicy resolved = resolver.resolve(type);
        assertInstanceOf(ProductApproveDocumentPostPolicy.class, resolved);
    }

    @Test
    void delegatesToDefaultPostLogic() {
        DefaultDocumentPostPolicy defaultPolicy = mock(DefaultDocumentPostPolicy.class);
        ProductApproveDocumentPostPolicy policy = new ProductApproveDocumentPostPolicy(defaultPolicy);
        Document document = new Document();

        policy.post(document);

        verify(defaultPolicy).post(document);
    }
}
