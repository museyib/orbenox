package com.orbenox.erp.unit;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UnitDimension extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
}