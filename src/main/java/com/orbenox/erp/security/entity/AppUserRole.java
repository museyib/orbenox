package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppUserRole extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppRole appRole;
}