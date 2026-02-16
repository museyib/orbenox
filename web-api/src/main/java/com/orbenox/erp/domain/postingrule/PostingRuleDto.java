package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.enums.PartnerSide;

/**
 * DTO for {@link PostingRule}
 */
public record PostingRuleDto(Integer sequence,
                             Long transactionTypeId,
                             Long debitAccountId,
                             Long creditAccountId,
                             AmountSource amountSource,
                             PartnerSide partnerSide) {
}