package com.orbenox.erp.common.resource;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ResourceActionRepository extends JpaRepository<ResourceAction, Long> {


    @Modifying
    @Transactional
    @Query("DELETE FROM ResourceAction r WHERE r.resource.id = :resourceId and r.action.id IN :actions")
    void deleteByResourceIdAndActions(@Param("resourceId") Long resourceId, @Param("actions") Set<Long> actions);
}
