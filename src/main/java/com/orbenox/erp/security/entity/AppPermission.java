package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.common.resource.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppPermission extends BaseCardEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppRole appRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser appUser;

    public String getPermissionCode() {
        return resource.getCode() + ":" + action.getCode();
    }
}