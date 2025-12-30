package com.orbenox.erp.domain.resource;

import com.orbenox.erp.domain.action.ActionDto;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Resource}
 */
public record ResourceDto(Long id, String code, String name, Set<ActionDto> actions,
                          Boolean enabled) implements Serializable {
}