package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.policy.RootPolicy;

import java.util.List;

public class PolicyResolver<T extends RootPolicy> {

    private final List<T> policies;

    public PolicyResolver(List<T> policies) {
        this.policies = policies;
    }

    public T resolve(TransactionType type) {
        return policies.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Not found proper policy for this transaction type: " + type.getName()));
    }
}