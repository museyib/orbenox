package com.orbenox.erp.security.projection;

import com.orbenox.erp.domain.resource.ResourceItem;

public interface PermissionItem {
    Long getId();

    ResourceItem getResource();

    String getAction();

    String getPermissionCode();

    boolean isEnabled();
}
