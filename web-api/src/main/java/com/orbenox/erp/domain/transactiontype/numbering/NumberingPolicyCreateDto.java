package com.orbenox.erp.domain.transactiontype.numbering;

import com.orbenox.erp.enums.ResetPeriod;

/**
 * DTO for {@link NumberingPolicy}
 */
public record NumberingPolicyCreateDto(Long typeId,
                                       String prefix,
                                       ResetPeriod resetPeriod,
                                       int sequenceLength) {
}
