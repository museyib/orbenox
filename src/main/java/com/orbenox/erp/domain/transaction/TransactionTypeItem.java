package com.orbenox.erp.domain.transaction;

public interface TransactionTypeItem {
    Long getId();
    String getCode();
    String getName();
    boolean isEnabled();
}
