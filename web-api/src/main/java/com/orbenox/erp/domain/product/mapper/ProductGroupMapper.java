package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class ProductGroupMapper {

    @PersistenceContext
    private EntityManager em;

    @Mapping(target = "parent", source = "parentId")
    public abstract ProductGroup toEntity(ProductGroupCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "parent", source = "parentId")
    public abstract void updateEntityFromDto(ProductGroupUpdateDto dto, @MappingTarget ProductGroup entity);

    public ProductGroup mapToProductGroup(Long value) {
        return value == 0 ? null : em.find(ProductGroup.class, value);
    }
}
