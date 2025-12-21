package com.orbenox.erp.unit.unitdimension;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UnitDimension extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
}