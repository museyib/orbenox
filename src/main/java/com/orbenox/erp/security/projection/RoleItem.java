package com.orbenox.erp.security.projection;

public interface RoleItem {
    Long getId();

    String getCode();

    String getName();

    boolean isEnabled();
}
