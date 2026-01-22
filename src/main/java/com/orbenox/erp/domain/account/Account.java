package com.orbenox.erp.domain.account;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.enums.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Account extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;
}