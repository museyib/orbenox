package com.orbenox.erp.unitofmeasure;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UnitOfMeasure extends BaseEntity {
    private String code;
    private String description;
}