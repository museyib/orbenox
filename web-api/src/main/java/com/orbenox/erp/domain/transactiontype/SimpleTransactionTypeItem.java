package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicyItem;
import com.orbenox.erp.enums.StockAffectDirection;

public interface SimpleTransactionTypeItem {
    Long getId();
    String getCode();
    String getName();
}
