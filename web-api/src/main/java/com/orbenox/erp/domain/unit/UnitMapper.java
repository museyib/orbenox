package com.orbenox.erp.domain.unit;

import com.orbenox.erp.domain.unit.unitdimension.UnitDimension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class UnitMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "unitDimension", source = "unitDimensionId")
    public abstract Unit toEntity(UnitCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "unitDimension", source = "unitDimensionId")
    public abstract void updateEntityFromDto(UnitUpdateDto dto, @MappingTarget Unit entity);

    UnitDimension mapToUnitDimension(Long value) {
        return value == 0 ? null : entityManager.getReference(UnitDimension.class, value);
    }
}
