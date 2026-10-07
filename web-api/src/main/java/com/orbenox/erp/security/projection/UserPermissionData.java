package com.orbenox.erp.security.projection;

import lombok.Data;

import java.util.List;

@Data
public class UserPermissionData {
    private SimpleUserItem user;
    private List<PermissionItem> permissions;
}
