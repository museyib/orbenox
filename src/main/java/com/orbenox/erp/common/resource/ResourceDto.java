package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionDto;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Resource}
 */
public record ResourceDto(Long id, String code, String name, Set<ActionDto> actions) implements Serializable {
}