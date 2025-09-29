package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppRole extends BaseEntity {
    private String roleName;
}