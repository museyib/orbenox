package com.orbenox.erp.domain.postingrule;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface PostingRuleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "debitAccount", ignore = true)
    @Mapping(target = "creditAccount", ignore = true)
    PostingRule toEntity(PostingRuleDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "debitAccount", ignore = true)
    @Mapping(target = "creditAccount", ignore = true)
    void updateEntityFromDto(PostingRuleDto dto, @MappingTarget PostingRule entity);
}
