package com.orbenox.erp.security.projection;

public interface UserItem {
    Long getId();

    String getUsername();

    String getPassword();

    String getDisplayName();

    UserTypeItem getUserType();

    boolean isEnabled();
}
