package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findByRootAndDeleted(boolean root, Boolean deleted);

    Optional<AppUser> findByIdAndRootAndDeleted(Long id, boolean root, Boolean deleted);

    AppUser findByIdAndDeleted(Long id, Boolean deleted);
}
