package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.country.CountryUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;


/**
 * DTO for {@link Product}
 */
public record ProductUpdateDto(Long id,
                               boolean enabled,
                               @NotBlank(message = "{code.notBlank}") String code,
                               @NotBlank(message = "{name.notBlank}") String name,
                               String description,
                               @NotNull(message = "{brand.notNull}") BrandUpdateDto brand,
                               @NotNull(message = "{productType.notNull}") ProductTypeUpdateDto productType,
                               @NotNull(message = "{productClass.notNull}") ProductClassUpdateDto productClass,
                               @NotNull(message = "{productGroup.notNull}") ProductGroupUpdateDto productGroup,
                               @NotNull(message = "{productCategory.notNull}") ProductCategoryUpdateDto productCategory,
                               @NotNull(message = "{producer.notNull}") ProducerUpdateDto producer,
                               @NotNull(message = "{country.notNull}") CountryUpdateDto country,
                               @NotNull(message = "{unit.notNull}") UnitUpdateDto defaultUnit,
                               @NotBlank(message = "{barcode.notNull}") String defaultBarcode) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductUpdateDto that = (ProductUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}