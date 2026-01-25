package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.projection.PermissionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<AppPermission, Long> {
    @Query("""
            SELECT p.id as id,
                   p.resource as resource,
                   p.action as action,
                   concat(p.resource.code, ':', p.action) as permissionCode,
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
                   concat(p.resource.code, ':', p.action) as permissionCode,
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
    List<String> getActionItemsByAppUserIdAndResourceId(@Param("appUserId") Long appUserId,
                                                            @Param("resourceId") Long resourceId);

    @Query("""
            SELECT p.action
            FROM AppPermission p
                WHERE p.appRole.id = :appRoleId
                    AND p.resource.id = :resourceId
                    AND p.deleted = false
            ORDER BY p.id""")
    List<String> getActionItemsByAppRoleIdAndResourceId(@Param("appRoleId") Long appRoleId,
                                                            @Param("resourceId") Long resourceId);
}
