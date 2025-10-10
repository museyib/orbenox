package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppUserRole;
import com.orbenox.erp.security.entity.AppUserRoleId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface AppUserRoleRepository extends JpaRepository<AppUserRole, AppUserRoleId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM AppUserRole r WHERE r.appUser.id = :userId and r.appRole.id IN :roles")
    void deleteByAppUserIdAndAppRole(@Param("userId") Long userId, @Param("roles") Set<Long> roles);
}
