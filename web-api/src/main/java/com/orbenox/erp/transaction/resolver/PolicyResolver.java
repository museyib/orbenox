package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.policy.RootPolicy;

import java.util.List;

public class PolicyResolver<T extends RootPolicy> {

    private final List<T> policies;

    public PolicyResolver(List<T> policies) {
        this.policies = policies;
    }

    public T resolve(TransactionType type) {
        policies.forEach(p -> System.out.println(p.getClass()));
        return policies.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow();
    }
}