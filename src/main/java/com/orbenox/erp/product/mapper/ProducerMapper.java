package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProducerDto;
import com.orbenox.erp.product.entity.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProducerMapper {
    ProducerDto toDto(Producer entity);
    Producer toEntity(ProducerDto dto);
    List<ProducerDto> toDtoList(List<Producer> entityList);
    void updateEntityFromDto(ProducerDto dto, @MappingTarget Producer entity);
}
