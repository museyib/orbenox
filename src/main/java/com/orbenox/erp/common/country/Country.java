package com.orbenox.erp.common.country;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class Country extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
}