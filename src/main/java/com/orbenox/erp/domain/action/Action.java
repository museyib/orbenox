package com.orbenox.erp.domain.action;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Action extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
}