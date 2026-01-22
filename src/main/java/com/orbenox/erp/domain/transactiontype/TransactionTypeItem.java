package com.orbenox.erp.domain.transactiontype;

public interface TransactionTypeItem {
    Long getId();
    String getCode();
    String getName();
    boolean isEnabled();
}
