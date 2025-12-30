package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Brand;

import java.io.Serializable;

/**
 * DTO for {@link Brand}
 */
public record BrandDto(Long id, Boolean enabled, String code, String name, String description) implements Serializable {
}