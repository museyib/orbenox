package com.orbenox.erp.transaction.policy.create;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class SalesOrderDocumentCreatePolicy implements DocumentCreatePolicy {
    private final DefaultDocumentCreatePolicy defaultPolicy;

    @Override
    public boolean supports(TransactionType type) {
        return "SALES_ORDER".equals(type.getCode());
    }

    @Override
    public void apply(Document document, CreateDocumentCommand command) {
        defaultPolicy.apply(document, command);
    }
}
