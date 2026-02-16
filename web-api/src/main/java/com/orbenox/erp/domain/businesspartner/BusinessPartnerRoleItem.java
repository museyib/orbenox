package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerRole;

public interface BusinessPartnerRoleItem {
    Long getId();
    Long getPartnerId();
    String getPartnerCode();
    String getPartnerName();
    PartnerRole getRole();
    boolean isEnabled();
}
