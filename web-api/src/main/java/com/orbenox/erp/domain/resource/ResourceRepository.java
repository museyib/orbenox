package com.orbenox.erp.domain.resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    Slice<ResourceItem> getAllItems(Pageable pageable);

    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM Resource r
            WHERE (LOWER(r.code) LIKE %:search% OR LOWER(r.name) LIKE %:search%)
                    AND r.deleted = false
            ORDER BY r.id""")
    Slice<ResourceItem> getItemsSearched(Pageable pageable, @Param("search") String search);


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
            SELECT r.actions
            FROM Resource r
            WHERE r.id = :resourceId""")
    List<String> getActionItemsByResourceId(@Param("resourceId") Long resourceId);

    Resource findByIdAndDeletedFalse(Long id);
}
