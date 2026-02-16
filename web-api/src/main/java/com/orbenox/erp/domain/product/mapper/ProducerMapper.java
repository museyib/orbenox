package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProducerCreateDto;
import com.orbenox.erp.domain.product.dto.ProducerUpdateDto;
import com.orbenox.erp.domain.product.entity.Producer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProducerMapper {
    Producer toEntity(ProducerCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(ProducerUpdateDto dto, @MappingTarget Producer entity);
}
