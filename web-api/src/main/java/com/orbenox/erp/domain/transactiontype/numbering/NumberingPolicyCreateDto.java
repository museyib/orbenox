package com.orbenox.erp.domain.transactiontype.numbering;

import com.orbenox.erp.enums.ResetPeriod;

import java.io.Serializable;

/**
 * DTO for {@link NumberingPolicy}
 */
public record NumberingPolicyCreateDto(Long typeId,
                                       String prefix,
                                       ResetPeriod resetPeriod,
                                       int sequenceLength) { }