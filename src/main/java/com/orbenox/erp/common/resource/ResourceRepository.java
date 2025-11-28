package com.orbenox.erp.common.resource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllByDeletedFalse();

    Resource findByIdAndDeletedFalse(Long id);
}
