package com.orbenox.erp.domain.currency;

public interface CurrencyItem {
    Long getId();

    String getCode();

    String getName();

    boolean isEnabled();
}
