package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    private String code;
    private String description;
    private Set<ActionDto> actions;
}