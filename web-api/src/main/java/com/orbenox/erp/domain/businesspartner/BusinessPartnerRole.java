package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.enums.PartnerRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BusinessPartnerRole extends BaseCardEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BusinessPartner partner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerRole role;
}