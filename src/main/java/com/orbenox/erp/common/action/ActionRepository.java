package com.orbenox.erp.common.action;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> findAllByDeletedFalse();

    Action findByIdAndDeletedFalse(Long id);
}
