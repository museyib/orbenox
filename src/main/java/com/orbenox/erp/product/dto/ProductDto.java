package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.unit.UnitDto;

import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
public record ProductDto(Long id, Boolean enabled, String code, String name, String description, BrandDto brand,
                         CategoryDto category, ProducerDto producer, UnitDto defaultUnit) implements Serializable {
}