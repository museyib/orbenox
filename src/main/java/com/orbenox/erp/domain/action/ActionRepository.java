package com.orbenox.erp.domain.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    Action findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT a.id as id,
                a.code as code,
                a.name as name,
                a.enabled as enabled
            FROM Action a
            WHERE a.deleted = false
            ORDER BY a.id""")
    List<ActionItem> getAllItems();
}
