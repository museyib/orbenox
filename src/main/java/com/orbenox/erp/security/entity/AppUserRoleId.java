package com.orbenox.erp.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class AppUserRoleId implements Serializable {
    @Serial
    private static final long serialVersionUID = -2227612613502692339L;
    @NotNull
    @Column(name = "app_user_id", nullable = false)
    private Long appUserId;

    @NotNull
    @Column(name = "app_role_id", nullable = false)
    private Long appRoleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUserRoleId entity = (AppUserRoleId) o;
        return Objects.equals(this.appRoleId, entity.appRoleId) &&
                Objects.equals(this.appUserId, entity.appUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appRoleId, appUserId);
    }

}