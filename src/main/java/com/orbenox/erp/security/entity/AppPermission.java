package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.resource.Resource;
import com.orbenox.erp.enums.Action;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppPermission extends BaseCardEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Resource resource;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppRole appRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser appUser;
}