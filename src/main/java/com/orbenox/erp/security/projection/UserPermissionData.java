package com.orbenox.erp.security.projection;

import lombok.Data;

import java.util.List;

@Data
public class UserPermissionData {
    private UserItem user;
    private List<PermissionItem> permissions;
}
