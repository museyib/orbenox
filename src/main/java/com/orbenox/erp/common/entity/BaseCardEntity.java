package com.orbenox.erp.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseCardEntity extends BaseEntity {
    private boolean enabled = true;
    private boolean deleted = false;
}
