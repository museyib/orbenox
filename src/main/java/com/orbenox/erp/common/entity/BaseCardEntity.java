package com.orbenox.erp.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseCardEntity extends BaseEntity {
    private Boolean enabled = true;
    private Boolean deleted = false;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
