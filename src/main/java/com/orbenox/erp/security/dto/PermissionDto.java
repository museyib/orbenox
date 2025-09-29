package com.orbenox.erp.security.dto;

import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.common.resource.ResourceDto;
import lombok.Data;

@Data
public class PermissionDto {
    private Long id;
    private ResourceDto resource;
    private ActionDto action;

    public String getPermissionCode() {
        return resource.getCode() + ":" + action.getCode();
    }
}
