package com.orbenox.erp.common.country;

import com.orbenox.erp.common.resource.Resource;

import java.io.Serializable;

/**
 * DTO for {@link Resource}
 */
public record CountryDto(Long id, String code, String name, Boolean enabled) implements Serializable {
}