package com.orbenox.erp.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user_role")
public class AppUserRole {
    @EmbeddedId
    private AppUserRoleId id;

    @MapsId("appUserId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @MapsId("appRoleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_role_id", nullable = false)
    private AppRole appRole;

}