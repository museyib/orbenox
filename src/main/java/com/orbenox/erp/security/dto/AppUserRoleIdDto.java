package com.orbenox.erp.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.orbenox.erp.security.entity.AppUserRoleId}
 */
@Value
public class AppUserRoleIdDto implements Serializable {
    @NotNull
    Long appUserId;
    @NotNull
    Long appRoleId;
}