package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.projection.RoleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<AppRole, Long> {

    AppRole findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM AppRole r
            WHERE r.deleted = false
            ORDER BY r.id""")
    List<RoleItem> getAllItems();

    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM AppRole r
            WHERE r.deleted = false AND r.enabled = true
            ORDER BY r.id""")
    List<RoleItem> getEnabledItems();


    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM AppRole r
            WHERE r.id = :id AND r.deleted = false
            ORDER BY r.id""")
    RoleItem getItemById(@Param("id") Long id);
}
