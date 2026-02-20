package com.orbenox.erp.domain.transactiontype.numbering;

import com.orbenox.erp.enums.ResetPeriod;

public interface NumberingPolicyItem {
    Long getId();
    String getPrefix();
    ResetPeriod getResetPeriod();
    int getSequenceLength();
}
