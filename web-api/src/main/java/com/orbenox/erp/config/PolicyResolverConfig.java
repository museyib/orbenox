package com.orbenox.erp.config;

import com.orbenox.erp.transaction.policy.approval.ApprovalPolicy;
import com.orbenox.erp.transaction.policy.create.DocumentCreatePolicy;
import com.orbenox.erp.transaction.policy.post.DocumentPostPolicy;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PolicyResolverConfig {

    @Bean
    public PolicyResolver<ApprovalPolicy> approvalPolicyResolver(List<ApprovalPolicy> policies) {
        return new PolicyResolver<>(policies);
    }

    @Bean
    public PolicyResolver<DocumentCreatePolicy> documentCreatePolicyResolver(List<DocumentCreatePolicy> policies) {
        return new PolicyResolver<>(policies);
    }

    @Bean
    public PolicyResolver<DocumentPostPolicy> documentPostPolicyResolver(List<DocumentPostPolicy> policies) {
        return new PolicyResolver<>(policies);
    }
}
