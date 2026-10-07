package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.policy.RootPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PolicyResolverTest {

    @Test
    void resolve_shouldReturnFirstSupportingPolicy() {
        RootPolicy expectedPolicy = new TestPolicy("SALES_ORDER");
        PolicyResolver<RootPolicy> resolver = new PolicyResolver<>(List.of(
                new TestPolicy("PRODUCT_APPROVE"),
                expectedPolicy,
                new TestPolicy("SALES_ORDER")
        ));

        TransactionType type = new TransactionType();
        type.setCode("SALES_ORDER");

        RootPolicy resolved = resolver.resolve(type);

        assertThat(resolved).isSameAs(expectedPolicy);
    }

    @Test
    void resolve_shouldThrowWhenNoPolicyMatches() {
        PolicyResolver<RootPolicy> resolver = new PolicyResolver<>(List.of(new TestPolicy("PRODUCT_APPROVE")));
        TransactionType type = new TransactionType();
        type.setCode("SALES_ORDER");
        type.setName("Sales Order");

        assertThatThrownBy(() -> resolver.resolve(type))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Not found proper policy for this transaction type: Sales Order");
    }

    private record TestPolicy(String supportedCode) implements RootPolicy {
        @Override
        public boolean supports(TransactionType type) {
            return supportedCode.equals(type.getCode());
        }
    }
}
