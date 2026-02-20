package com.orbenox.erp.domain.transactiontype.numbering;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NumberingPolicyRepository extends JpaRepository<NumberingPolicy, Long> {
    Optional<NumberingPolicy> findByTypeId(Long typeId);
}
