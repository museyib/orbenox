package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findByRootFalseAndDeletedFalse();

    Optional<AppUser> findByIdAndRootFalseAndDeletedFalse(Long id);

    AppUser findByIdAndDeletedFalse(Long id);
}
