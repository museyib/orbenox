package com.orbenox.erp.security.repository;

import com.orbenox.erp.security.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findByRoot(boolean root);

    Optional<AppUser> findByIdAndRoot(Long id, boolean root);
}
