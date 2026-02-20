package com.orbenox.erp.domain.transactiontype.numbering;

import com.orbenox.erp.enums.ResetPeriod;

/**
 * DTO for {@link NumberingPolicy}
 */
public record NumberingPolicyUpdateDto(Long id,
                                       Long typeId,
                                       String prefix,
                                       ResetPeriod resetPeriod,
                                       int sequenceLength) { }