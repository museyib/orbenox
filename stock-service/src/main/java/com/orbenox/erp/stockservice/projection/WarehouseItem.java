package com.orbenox.erp.stockservice.projection;

public interface WarehouseItem {
    Long getId();
    String getCode();
    String getName();
    boolean isEnabled();
}
