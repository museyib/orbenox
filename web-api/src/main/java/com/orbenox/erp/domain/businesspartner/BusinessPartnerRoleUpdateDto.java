package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerRole;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * DTO for {@link BusinessPartnerRole}
 */
public record BusinessPartnerRoleUpdateDto(Long id,
                                           boolean enabled,
                                           @NotNull(message = "{partner.notNull}") Long partnerId,
                                           @NotNull(message = "{partnerRole.notNull}") PartnerRole role) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BusinessPartnerRoleUpdateDto that = (BusinessPartnerRoleUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
