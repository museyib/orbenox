package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionItem;
import lombok.Data;

import java.util.List;

@Data
public class ResourceData {
    private ResourceItem resource;
    private List<ActionItem> actions;
}

