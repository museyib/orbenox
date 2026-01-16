package com.orbenox.erp.domain.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Query("""
        SELECT w.id AS id,
            w.code AS code,
            w.name AS name,
            w.enabled AS enabled
        FROM Warehouse w
        WHERE w.deleted = false
        ORDER BY w.id""")
    List<WarehouseItem> getAllItems();

    @Query("""
        SELECT w.id AS id,
            w.code AS code,
            w.name AS name,
            w.enabled AS enabled
        FROM Warehouse w
        WHERE w.deleted = false AND w.enabled = true
        ORDER BY w.id""")
    List<WarehouseItem> getEnabledItems();

    @Query("""
            SELECT w.id as id,
                   w.code as code,
                   w.name as name,
                   w.enabled as enabled
            FROM Warehouse w
            WHERE w.id = :id AND w.deleted = false
            """)
    WarehouseItem getItemById(@Param("id") Long id);

    Warehouse findByIdAndDeletedFalse(Long id);
}
