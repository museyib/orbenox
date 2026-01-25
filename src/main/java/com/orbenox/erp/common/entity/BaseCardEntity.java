package com.orbenox.erp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseCardEntity extends BaseEntity {

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Boolean deleted = false;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
