package com.orbenox.erp.currency;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Currency extends BaseCardEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;
}