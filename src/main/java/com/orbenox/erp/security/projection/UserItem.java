package com.orbenox.erp.security.projection;

import com.orbenox.erp.security.enums.UserType;

public interface UserItem {
    Long getId();

    String getUsername();

    String getDisplayName();

    UserType getUserType();

    boolean isEnabled();
}
