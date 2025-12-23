package com.orbenox.erp.currency;

public interface CurrencyItem {
    Long getId();

    String getCode();

    String getName();

    boolean isEnabled();
}
