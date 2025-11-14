package com.orbenox.erp.common.action;

import java.io.Serializable;

/**
 * DTO for {@link for Action}
 */
public record ActionDto(Long id, String code, String name) implements Serializable {
}