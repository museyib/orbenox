package com.orbenox.erp.domain.resource;

import lombok.Data;

import java.util.List;

@Data
public class ResourceData {
    private ResourceItem resource;
    private List<String> actions;
}

