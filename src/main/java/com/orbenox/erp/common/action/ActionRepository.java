package com.orbenox.erp.common.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> findAllByDeleted(Boolean deleted);

    Action findByIdAndDeleted(Long id, Boolean deleted);
}
