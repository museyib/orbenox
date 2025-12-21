package com.orbenox.erp.security.projection;

import com.orbenox.erp.common.action.ActionItem;
import com.orbenox.erp.common.resource.ResourceItem;

public interface PermissionItem {
    Long getId();

    ResourceItem getResource();

    ActionItem getAction();

    String getPermissionCode();

    boolean isEnabled();
}
