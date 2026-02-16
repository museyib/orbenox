package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerRole;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link BusinessPartnerRole}
 */
public record BusinessPartnerRoleCreateDto(boolean enabled,
                                           @NotNull(message = "{partner.notNull}") Long partnerId,
                                           @NotNull(message = "{partnerRole.notNull}") PartnerRole role) {
}
