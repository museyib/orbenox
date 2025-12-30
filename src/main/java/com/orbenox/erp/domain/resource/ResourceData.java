package com.orbenox.erp.domain.resource;

import com.orbenox.erp.domain.action.ActionItem;
import lombok.Data;

import java.util.List;

@Data
public class ResourceData {
    private ResourceItem resource;
    private List<ActionItem> actions;
}

