package com.orbenox.erp.security.entity;

import java.io.Serializable;

/**
 * DTO for {@link UserType}
 */
public record UserTypeDto(Long id, Boolean enabled, String code, String name) implements Serializable {
}