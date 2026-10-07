package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.postingrule.PostingRuleDto;
import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicyUpdateDto;
import com.orbenox.erp.enums.StockAffectDirection;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link TransactionType}
 */
public record TransactionTypeUpdateDto(Long id,
                                       boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       StockAffectDirection stockAffectDirection,
                                       boolean commercialAffected,
                                       boolean accountingAffected,
                                       boolean creditLimitChecked,
                                       boolean approvalRequired,
                                       NumberingPolicyUpdateDto numberingPolicy,
                                       Set<PostingRuleDto> rules) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTypeUpdateDto that = (TransactionTypeUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}