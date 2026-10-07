package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * DTO for {@link BusinessPartner}
 */
public record BusinessPartnerUpdateDto(Long id,
                                       boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       String taxId,
                                       @NotNull(message = "{partnerType.notNull}") PartnerType type) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BusinessPartnerUpdateDto that = (BusinessPartnerUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
