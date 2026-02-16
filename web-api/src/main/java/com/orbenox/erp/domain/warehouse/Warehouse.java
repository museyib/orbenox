package com.orbenox.erp.domain.warehouse;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Warehouse extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;
}