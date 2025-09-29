package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.common.resource.Resource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppPermission extends BaseEntity {
    @ManyToOne
    private Resource resource;

    @ManyToOne
    private Action action;

    @ManyToOne
    private AppRole appRole;

    @ManyToOne
    private AppUser appUser;

    public String getPermissionCode() {
        return resource.getCode() + ":" + action.getCode();
    }
}