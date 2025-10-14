package com.orbenox.erp.security.repository;

import com.orbenox.erp.common.resource.Resource;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.entity.AppRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<AppPermission,Long> {
    List<AppPermission> findByAppUser(AppUser appUser);
    List<AppPermission> findByAppRole(AppRole appRole);
    List<AppPermission> findByAppUserAndResource(AppUser appUser, Resource resource);

    @Modifying
    @Transactional
    @Query("DELETE FROM AppPermission p WHERE p.appUser.id = :userId AND CONCAT(p.resource.code, ':', p.action.code) IN :codes")
    void deleteByAppUserIdAndCodes(@Param("userId") Long  userId, @Param("codes") Set<String> codes);

    @Modifying
    @Transactional
    @Query("DELETE FROM AppPermission p WHERE p.appRole.id = :roleId AND CONCAT(p.resource.code, ':', p.action.code) IN :codes")
    void deleteByAppRoleIdAndCodes(@Param("roleId") Long  roleId, @Param("codes") Set<String> codes);

    List<AppPermission> findByAppUserId(Long appUserId);

    List<AppPermission> findByAppRoleAndResource(AppRole appRole, Resource resource);

    List<AppPermission> findByAppRoleId(Long appRoleId);
}
