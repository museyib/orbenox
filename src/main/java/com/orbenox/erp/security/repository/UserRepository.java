package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.projection.SimpleUserItem;
import com.orbenox.erp.security.projection.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    AppUser findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT u.id as id,
                u.username as username,
                u.displayName as displayName,
                u.userType as userType,
                u.enabled as enabled
            FROM AppUser u
            WHERE u.deleted = false AND u.root = false
            ORDER BY u.id""")
    List<SimpleUserItem> getAllItems();

    @Query("""
            SELECT u.id as id,
                u.username as username,
                u.displayName as displayName,
                u.userType as userType,
                u.enabled as enabled
            FROM AppUser u
            WHERE u.id = :id AND u.deleted = false AND u.root = false""")
    SimpleUserItem getItemById(@Param("id") Long id);

    @Query("""
            SELECT u.id as id,
                u.username as username,
                u.password as password,
                u.displayName as displayName,
                u.userType as userType,
                u.enabled as enabled
            FROM AppUser u
            WHERE u.username = :username AND u.deleted = false""")
    UserItem getItemByUsername(@Param("username") String username);

    @Query("""
            SELECT r.id as id,
                r.code as code,
                r.name as name,
                r.enabled as enabled
            FROM AppUser u
            LEFT JOIN u.roles as r
            WHERE u.id = :userId AND r.deleted = false AND u.root = false
            ORDER BY r.id""")
    List<RoleItem> getRolesByUserId(@Param("userId") Long userId);
}
