package com.orbenox.erp.transaction.policy.create;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DocumentCreatePolicyResolverTest {

    @Test
    void resolvesSalesOrderPolicy() {
        DefaultDocumentCreatePolicy defaultPolicy = mock(DefaultDocumentCreatePolicy.class);
        PolicyResolver<DocumentCreatePolicy> resolver = new PolicyResolver<>(List.of(
                new SalesOrderDocumentCreatePolicy(defaultPolicy),
                new ProductApproveDocumentCreatePolicy(defaultPolicy),
                defaultPolicy
        ));

        TransactionType type = new TransactionType();
        type.setCode("SALES_ORDER");

        DocumentCreatePolicy resolved = resolver.resolve(type);
        assertInstanceOf(SalesOrderDocumentCreatePolicy.class, resolved);
    }

    @Test
    void resolvesProductApprovePolicy() {
        DefaultDocumentCreatePolicy defaultPolicy = mock(DefaultDocumentCreatePolicy.class);
        PolicyResolver<DocumentCreatePolicy> resolver = new PolicyResolver<>(List.of(
                new SalesOrderDocumentCreatePolicy(defaultPolicy),
                new ProductApproveDocumentCreatePolicy(defaultPolicy),
                defaultPolicy
        ));

        TransactionType type = new TransactionType();
        type.setCode("PRODUCT_APPROVE");

        DocumentCreatePolicy resolved = resolver.resolve(type);
        assertInstanceOf(ProductApproveDocumentCreatePolicy.class, resolved);
    }

    @Test
    void delegatesToDefaultCreateLogic() {
        DefaultDocumentCreatePolicy defaultPolicy = mock(DefaultDocumentCreatePolicy.class);
        SalesOrderDocumentCreatePolicy policy = new SalesOrderDocumentCreatePolicy(defaultPolicy);
        Document document = new Document();
        CreateDocumentCommand command = new CreateDocumentCommand(
                LocalDate.now(),
                1L,
                "test",
                null,
                null,
                null,
                null,
                null,
                List.of()
        );

        policy.apply(document, command);

        verify(defaultPolicy).apply(document, command);
    }
}
