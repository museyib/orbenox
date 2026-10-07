package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.enums.PartnerSide;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link PostingRule}
 */
public record PostingRuleDto(Integer sequence,
                             @NotNull(message = "{transactionType.notNull}") Long transactionTypeId,
                             @NotNull(message = "{debitAccount.notNull}") Long debitAccountId,
                             @NotNull(message = "{creditAccount.notNull}") Long creditAccountId,
                             @NotNull(message = "{amountSource.notNull}") AmountSource amountSource,
                             @NotNull(message = "{partnerSide.notNull}") PartnerSide partnerSide) {
}
