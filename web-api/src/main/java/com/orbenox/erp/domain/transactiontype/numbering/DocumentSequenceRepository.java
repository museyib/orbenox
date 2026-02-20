package com.orbenox.erp.domain.transactiontype.numbering;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM DocumentSequence s
        WHERE s.type.id = :typeId
            AND s.year = :year
            AND s.month = :month""")
    Optional<DocumentSequence> findForUpdate(@Param("type") Long typeId,
                                                    @Param("year") int year,
                                                    @Param("month") int month);

}
