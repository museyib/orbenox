package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProducerDto;
import com.orbenox.erp.product.entity.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProducerMapper {
    Producer toEntity(ProducerDto dto);

    void updateEntityFromDto(ProducerDto dto, @MappingTarget Producer entity);
}
