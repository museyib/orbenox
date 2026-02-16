package com.orbenox.erp.domain.account;

import com.orbenox.erp.enums.AccountType;

public interface AccountItem {
    Long getId();
    String getCode();
    String getName();
    AccountType getAccountType();
    boolean isEnabled();
}
