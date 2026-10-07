package com.orbenox.erp.security.projection;

@SuppressWarnings("unused")
public interface SimpleUserItem {
    Long getId();

    String getUsername();

    String getDisplayName();

    UserTypeItem getUserType();

    boolean isEnabled();
}
