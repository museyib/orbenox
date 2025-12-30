package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.projection.PermissionItem;
import com.orbenox.erp.security.projection.UserItem;
import lombok.Data;

import java.util.List;

@Data
public class UserPermissionData {
    private UserItem user;
    private List<PermissionItem> permissions;
}
