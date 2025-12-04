package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.unit.UnitDto;

import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
public record ProductDto(Long id,
                         Boolean enabled,
                         String code,
                         String name,
                         String description,
                         BrandDto brand,
                         ProductTypeDto productType,
                         ProductClassDto productClass,
                         ProductGroupDto productGroup,
                         ProductCategoryDto productCategory,
                         ProducerDto producer,
                         UnitDto defaultUnit,
                         String defaultBarcode) implements Serializable {
}