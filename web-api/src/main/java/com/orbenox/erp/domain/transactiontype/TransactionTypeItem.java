package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.enums.StockAffectDirection;

public interface TransactionTypeItem {
    Long getId();
    String getCode();
    String getName();
    String getDocumentNoPrefix();
    StockAffectDirection getStockAffectDirection();
    boolean isCommercialAffected();
    boolean isAccountingAffected();
    boolean isApprovalRequired();
    boolean isEnabled();
}
