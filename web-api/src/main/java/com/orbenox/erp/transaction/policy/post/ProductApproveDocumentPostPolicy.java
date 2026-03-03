package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class ProductApproveDocumentPostPolicy implements DocumentPostPolicy {
    private final DefaultDocumentPostPolicy defaultPolicy;

    @Override
    public boolean supports(TransactionType type) {
        return "PRODUCT_APPROVE".equals(type.getCode());
    }

    @Override
    public void post(Document document) {
        defaultPolicy.post(document);
    }
}
