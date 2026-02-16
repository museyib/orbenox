package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerType;

public interface BusinessPartnerItem {
    Long getId();
    String getCode();
    String getName();
    String getTaxId();
    PartnerType getType();
    boolean isEnabled();
}
