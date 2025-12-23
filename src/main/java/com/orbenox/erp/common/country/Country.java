package com.orbenox.erp.common.country;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Country extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
}