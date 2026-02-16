package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.enums.PartnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BusinessPartner extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerType type;
}