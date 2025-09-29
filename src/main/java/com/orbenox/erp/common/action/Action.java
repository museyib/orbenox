package com.orbenox.erp.common.action;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Action extends BaseEntity {
    private String code;
    private String description;
}