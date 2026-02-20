package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.transactiontype.numbering.NumberingPolicyItem;
import com.orbenox.erp.enums.StockAffectDirection;

public interface TransactionTypeItem {
    Long getId();
    String getCode();
    String getName();
    StockAffectDirection getStockAffectDirection();
    boolean isCommercialAffected();
    boolean isAccountingAffected();
    boolean isCreditLimitChecked();
    boolean isApprovalRequired();
    boolean isEnabled();
    NumberingPolicyItem getNumberingPolicy();
}
