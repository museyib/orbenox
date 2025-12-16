package com.orbenox.erp.common.resource;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @EntityGraph(attributePaths = "actions")
    List<Resource> findAllByDeletedFalseOrderByIdAsc();

    @EntityGraph(attributePaths = "actions")
    Resource findByIdAndDeletedFalse(Long id);
}
