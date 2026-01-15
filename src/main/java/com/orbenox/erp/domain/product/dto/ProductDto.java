package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.country.CountryDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.unit.UnitDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
public record ProductDto(Long id,
                         Boolean enabled,
                         @NotBlank(message = "{code.notBlank}")
                         String code,
                         @NotBlank(message = "{name.notBlank}")
                         String name,
                         String description,
                         @NotNull(message = "{brand.notNull}")
                         BrandDto brand,
                         @NotNull(message = "{productType.notNull}")
                         ProductTypeDto productType,
                         @NotNull(message = "{productClass.notNull}")
                         ProductClassDto productClass,
                         @NotNull(message = "{productGroup.notNull}")
                         ProductGroupDto productGroup,
                         @NotNull(message = "{productCategory.notNull}")
                         ProductCategoryDto productCategory,
                         @NotNull(message = "{producer.notNull}")
                         ProducerDto producer,
                         @NotNull(message = "{country.notNull}")
                         CountryDto country,
                         @NotNull(message = "{defaultUnit.notNull}")
                         UnitDto defaultUnit,
                         @NotBlank(message = "{defaultBarcode.notNull}")
                         String defaultBarcode) implements Serializable {
}