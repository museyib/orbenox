package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.UserType;
import com.orbenox.erp.security.projection.UserTypeItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserTypeRepository extends CrudRepository<UserType, Integer> {

    @Query("""
            SELECT t.id as id,
                t.code as code,
                t.name as name,
                t.enabled as enabled
            FROM UserType t
            WHERE t.deleted = false AND t.enabled = true
            ORDER BY t.id""")
    List<UserTypeItem> getEnabledItems();
}
