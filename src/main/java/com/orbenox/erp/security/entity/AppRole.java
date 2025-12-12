package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppRole extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
}