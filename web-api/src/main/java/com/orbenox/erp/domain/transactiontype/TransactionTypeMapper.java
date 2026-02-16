package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.domain.postingrule.PostingRuleDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class TransactionTypeMapper {

    @PersistenceContext
    private EntityManager em;

    @Mapping(target = "rules", source = "rules")
    public abstract TransactionType toEntity(TransactionTypeCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    public abstract void updateEntityFromDto(TransactionTypeUpdateDto dto, @MappingTarget TransactionType entity);

    Set<PostingRule> mapToRules(Set<PostingRuleDto> dtoList) {
        Set<PostingRule> rules = new HashSet<>();
        for (PostingRuleDto dto : dtoList) {
            PostingRule rule = new PostingRule();
            rule.setSequence(dto.sequence());
            rule.setDebitAccount(em.getReference(Account.class, dto.debitAccountId()));
            rule.setCreditAccount(em.getReference(Account.class, dto.creditAccountId()));
            rule.setAmountSource(dto.amountSource());
            rule.setPartnerSide(dto.partnerSide());
            rules.add(rule);
        }

        return rules;
    }
}
