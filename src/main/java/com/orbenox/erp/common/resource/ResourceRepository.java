package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM Resource r
            WHERE r.deleted = false
            ORDER BY r.id""")
    List<ResourceItem> getAllItems();

    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM Resource r
            WHERE r.id = :id AND r.deleted = false
            ORDER BY r.id""")
    ResourceItem getItemById(@Param("id") Long id);

    @Query("""
        SELECT a.id as id,
            a.code as code,
            a.name as name,
                coalesce(a.enabled, false) as enabled
        FROM Resource r
        LEFT JOIN r.actions a
        WHERE r.id = :resourceId""")
    List<ActionItem> getActionItemsByResourceId(@Param("resourceId") Long resourceId);

    Resource findByIdAndDeletedFalse(Long id);
}
