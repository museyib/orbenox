package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface RoleRepository  extends JpaRepository<AppRole,Long> {
    @NonNull
    Optional<AppRole> findById(@NonNull Long id);

    List<AppRole> findAllByDeleted(Boolean deleted);

    AppRole findByIdAndDeleted(Long id, Boolean deleted);
}
