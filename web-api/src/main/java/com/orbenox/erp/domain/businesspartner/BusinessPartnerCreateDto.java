package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link BusinessPartner}
 */
public record BusinessPartnerCreateDto(boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       String taxId,
                                       @NotNull(message = "{partnerType.notNull}") PartnerType type) {
}
