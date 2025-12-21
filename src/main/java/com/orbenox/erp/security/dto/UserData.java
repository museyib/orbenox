package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.projection.UserItem;
import lombok.Data;

import java.util.List;

@Data
public class UserData {
    private UserItem user;
    private List<RoleItem> roles;
}
