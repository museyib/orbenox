package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserType extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
}