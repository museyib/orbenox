package com.orbenox.erp.security.repository;

import com.orbenox.erp.domain.action.ActionItem;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.projection.PermissionItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<AppPermission, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM AppPermission p WHERE p.appUser.id = :userId AND CONCAT(p.resource.code, ':', p.action.code) IN :codes")
    void deleteByAppUserIdAndCodes(@Param("userId") Long userId, @Param("codes") Set<String> codes);

    @Modifying
    @Transactional
    @Query("DELETE FROM AppPermission p WHERE p.appRole.id = :roleId AND CONCAT(p.resource.code, ':', p.action.code) IN :codes")
    void deleteByAppRoleIdAndCodes(@Param("roleId") Long roleId, @Param("codes") Set<String> codes);

    List<AppPermission> findByAppRoleIdAndDeletedFalse(Long appRoleId);

    List<AppPermission> findByAppUserIdAndDeletedFalse(Long appUserId);

    @Query("""
            SELECT p.id as id,
                   p.resource as resource,
                   p.action as action,
                   concat(p.resource.code, ':', p.action.code) as permissionCode,
                   p.enabled as enabled
            FROM AppPermission p
            WHERE p.appUser.id = :userId
                    AND p.deleted = false
            ORDER BY p.id""")
    List<PermissionItem> getPermissionsByUserId(Long userId);

    @Query("""
            SELECT p.id as id,
                   p.resource as resource,
                   p.action as action,
                   concat(p.resource.code, ':', p.action.code) as permissionCode,
                   p.enabled as enabled
            FROM AppPermission p
            WHERE p.appRole.id = :roleId
                    AND p.deleted = false
            ORDER BY p.id""")
    List<PermissionItem> getPermissionsByRoleId(Long roleId);

    @Query("""
            SELECT p.action
            FROM AppPermission p
            WHERE p.appUser.id = :appUserId
                    AND p.resource.id = :resourceId
                    AND p.deleted = false
            ORDER BY p.id""")
    List<ActionItem> getActionItemsByAppUserIdAndResourceId(@Param("appUserId") Long appUserId,
                                                            @Param("resourceId") Long resourceId);

    @Query("""
            SELECT p.action
            FROM AppPermission p
                WHERE p.appRole.id = :appRoleId
                    AND p.resource.id = :resourceId
                    AND p.deleted = false
            ORDER BY p.id""")
    List<ActionItem> getActionItemsByAppRoleIdAndResourceId(@Param("appRoleId") Long appRoleId,
                                                            @Param("resourceId") Long resourceId);
}
