package com.orbenox.erp.warehouse;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Warehouse extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    private Boolean enabled;

    private Boolean deleted;
}