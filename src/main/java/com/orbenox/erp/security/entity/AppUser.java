package com.orbenox.erp.security.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.security.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class AppUser extends BaseEntity {
    private String username;
    private String password;
    private String displayName;

    @Enumerated(EnumType.STRING)
    private UserType userType;
    private boolean root;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_user_role",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_role_id")
    )
    private Set<AppRole> roles = new HashSet<>();
}