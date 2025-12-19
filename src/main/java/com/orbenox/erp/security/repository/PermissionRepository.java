package com.orbenox.erp.security.repository;

import com.orbenox.erp.common.action.ActionItem;
import com.orbenox.erp.security.entity.AppPermission;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<AppPermission,Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM AppPermission p WHERE p.appUser.id = :userId AND CONCAT(p.resource.code, ':', p.action.code) IN :codes")
    void deleteByAppUserIdAndCodes(@Param("userId") Long  userId, @Param("codes") Set<String> codes);

    @Modifying
    @Transactional
    @Query("DELETE FROM AppPermission p WHERE p.appRole.id = :roleId AND CONCAT(p.resource.code, ':', p.action.code) IN :codes")
    void deleteByAppRoleIdAndCodes(@Param("roleId") Long  roleId, @Param("codes") Set<String> codes);

    List<AppPermission> findByAppRoleIdAndDeletedFalse(Long appRoleId);

    List<AppPermission> findByAppUserIdAndDeletedFalse(Long appUserId);

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
        WHERE p.appRole.id = :appUserId
                AND p.resource.id = :resourceId
                AND p.deleted = false
        ORDER BY p.id""")
    List<ActionItem> getActionItemsByAppRoleIdAndResourceId(@Param("appRoleId") Long appRoleId,
                                                            @Param("resourceId") Long resourceId);
}
