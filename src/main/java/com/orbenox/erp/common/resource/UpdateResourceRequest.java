package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionDto;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateResourceRequest {
    Set<ActionDto> actionsToAdd;
    Set<ActionDto> actionsToRemove;
    private String code;
    private String name;
    private boolean enabled;
}
