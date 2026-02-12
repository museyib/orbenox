package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.policy.ApprovalPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApprovalPolicyResolver {

    private final List<ApprovalPolicy> policies;

    public ApprovalPolicy resolve(TransactionType type) {
        return policies.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow();
    }
}
