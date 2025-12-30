package com.orbenox.erp.domain.country;

import com.orbenox.erp.domain.resource.Resource;

import java.io.Serializable;

/**
 * DTO for {@link Resource}
 */
public record CountryDto(Long id, String code, String name, Boolean enabled) implements Serializable {
}