package com.orbenox.erp.domain.transactiontype;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface TransactionTypeMapper {
    TransactionType toEntity(TransactionTypeCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(TransactionTypeUpdateDto dto, @MappingTarget TransactionType entity);
}
