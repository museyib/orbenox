package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<AppRole,Long> {
    @NonNull
    Optional<AppRole> findById(@NonNull Long id);
    @NonNull
    Optional<AppRole> findByRoleName(@NonNull String name);
}
