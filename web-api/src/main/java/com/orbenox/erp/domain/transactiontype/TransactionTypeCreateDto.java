package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.postingrule.PostingRuleDto;
import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicyCreateDto;
import com.orbenox.erp.enums.StockAffectDirection;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * DTO for {@link TransactionType}
 */
public record TransactionTypeCreateDto(boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       StockAffectDirection stockAffectDirection,
                                       boolean commercialAffected,
                                       boolean accountingAffected,
                                       boolean creditLimitChecked,
                                       boolean approvalRequired,
                                       NumberingPolicyCreateDto numberingPolicy,
                                       Set<PostingRuleDto> rules) {
}