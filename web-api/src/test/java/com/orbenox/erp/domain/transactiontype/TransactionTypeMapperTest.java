package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.domain.postingrule.PostingRuleDto;
import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicy;
import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicyCreateDto;
import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicyUpdateDto;
import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.enums.PartnerSide;
import com.orbenox.erp.enums.ResetPeriod;
import com.orbenox.erp.enums.StockAffectDirection;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionTypeMapperTest {
    @Mock
    EntityManager entityManager;
    @InjectMocks
    TransactionTypeMapperImpl mapper;

    @Test
    void createMapsTransactionSettingsNumberingPolicyAndPostingRuleReferences() {
        Account debit = new Account();
        Account credit = new Account();
        when(entityManager.getReference(Account.class, 10L)).thenReturn(debit);
        when(entityManager.getReference(Account.class, 20L)).thenReturn(credit);
        PostingRuleDto ruleDto = new PostingRuleDto(1, 99L, 10L, 20L, AmountSource.NET, PartnerSide.DEBIT);
        NumberingPolicyCreateDto numbering = new NumberingPolicyCreateDto(null, "SO-", ResetPeriod.YEARLY, 7);

        TransactionType type = mapper.toEntity(new TransactionTypeCreateDto(true, "SALE", "Sales order",
                StockAffectDirection.OUT, true, true, false, true, numbering, Set.of(ruleDto)));

        assertThat(type.isEnabled()).isTrue();
        assertThat(type.getCode()).isEqualTo("SALE");
        assertThat(type.getName()).isEqualTo("Sales order");
        assertThat(type.getStockAffectDirection()).isEqualTo(StockAffectDirection.OUT);
        assertThat(type.isCommercialAffected()).isTrue();
        assertThat(type.isAccountingAffected()).isTrue();
        assertThat(type.isCreditLimitChecked()).isFalse();
        assertThat(type.isApprovalRequired()).isTrue();
        assertThat(type.getNumberingPolicy().getPrefix()).isEqualTo("SO-");
        assertThat(type.getNumberingPolicy().getResetPeriod()).isEqualTo(ResetPeriod.YEARLY);
        assertThat(type.getNumberingPolicy().getSequenceLength()).isEqualTo(7);
        assertThat(type.getRules()).hasSize(1);
        PostingRule mappedRule = type.getRules().iterator().next();
        assertThat(mappedRule.getSequence()).isEqualTo(1);
        assertThat(mappedRule.getDebitAccount()).isSameAs(debit);
        assertThat(mappedRule.getCreditAccount()).isSameAs(credit);
        assertThat(mappedRule.getAmountSource()).isEqualTo(AmountSource.NET);
        assertThat(mappedRule.getPartnerSide()).isEqualTo(PartnerSide.DEBIT);
        verify(entityManager).getReference(Account.class, 10L);
        verify(entityManager).getReference(Account.class, 20L);
    }

    @Test
    void updateChangesMappedFieldsAndNestedPolicyButLeavesRulesAlone() {
        TransactionType type = new TransactionType();
        type.setCode("OLD");
        type.setName("Old type");
        type.setRules(Set.of(new PostingRule()));
        NumberingPolicy policy = new NumberingPolicy();
        policy.setPrefix("OLD-");
        type.setNumberingPolicy(policy);
        Set<PostingRule> existingRules = type.getRules();

        mapper.updateEntityFromDto(new TransactionTypeUpdateDto(3L, true, "NEW", "New type",
                StockAffectDirection.IN, true, false, true, false,
                new NumberingPolicyUpdateDto(4L, null, "NEW-", ResetPeriod.MONTHLY, 5),
                Set.of()), type);

        assertThat(type.getId()).isEqualTo(3L);
        assertThat(type.isEnabled()).isTrue();
        assertThat(type.getCode()).isEqualTo("NEW");
        assertThat(type.getName()).isEqualTo("New type");
        assertThat(type.getStockAffectDirection()).isEqualTo(StockAffectDirection.IN);
        assertThat(type.isCommercialAffected()).isTrue();
        assertThat(type.isAccountingAffected()).isFalse();
        assertThat(type.isCreditLimitChecked()).isTrue();
        assertThat(type.isApprovalRequired()).isFalse();
        assertThat(type.getNumberingPolicy()).isSameAs(policy);
        assertThat(policy.getId()).isEqualTo(4L);
        assertThat(policy.getPrefix()).isEqualTo("NEW-");
        assertThat(policy.getResetPeriod()).isEqualTo(ResetPeriod.MONTHLY);
        assertThat(policy.getSequenceLength()).isEqualTo(5);
        assertThat(type.getRules()).isSameAs(existingRules);
        verifyNoInteractions(entityManager);
    }
}
