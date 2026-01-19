package com.orbenox.erp.security.projection;

import lombok.Data;

import java.util.List;

@Data
public class UserData {
    private SimpleUserItem user;
    private List<RoleItem> roles;
}
