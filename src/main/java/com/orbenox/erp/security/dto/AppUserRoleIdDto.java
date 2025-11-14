package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppUserRoleId;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link AppUserRoleId}
 */
public record AppUserRoleIdDto(@NotNull Long appUserId, @NotNull Long appRoleId) implements Serializable {
}