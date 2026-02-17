package com.orbenox.erp.domain.businesspartner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, Long> {
    @Query("""
            SELECT bp.id as id,
                   bp.code as code,
                   bp.name as name,
                   bp.taxId as taxId,
                   bp.type as type,
                   bp.enabled as enabled
            FROM BusinessPartner bp
            WHERE bp.deleted = false
            ORDER BY bp.id""")
    Slice<BusinessPartnerItem> getAllItems(Pageable pageable);

@Query("""
            SELECT bp.id as id,
                   bp.code as code,
                   bp.name as name,
                   bp.taxId as taxId,
                   bp.type as type,
                   bp.enabled as enabled
            FROM BusinessPartner bp
            WHERE  bp.deleted = false
                    AND (LOWER(bp.code) LIKE %:search% OR LOWER(bp.name) LIKE %:search% OR LOWER(bp.taxId) LIKE %:search%)
            ORDER BY bp.id""")
    Slice<BusinessPartnerItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT bp.id as id,
                   bp.code as code,
                   bp.name as name,
                   bp.taxId as taxId,
                   bp.type as type,
                   bp.enabled as enabled
            FROM BusinessPartner bp
            WHERE bp.deleted = false AND bp.enabled = true
            ORDER BY bp.id""")
    List<BusinessPartnerItem> getEnabledItems();

    @Query("""
            SELECT bp.id as id,
                   bp.code as code,
                   bp.name as name,
                   bp.taxId as taxId,
                   bp.type as type,
                   bp.enabled as enabled
            FROM BusinessPartner bp
            WHERE bp.id = :id AND bp.deleted = false
            """)
    BusinessPartnerItem getItemById(@Param("id") Long id);

    BusinessPartner findByIdAndDeletedFalse(Long id);
}

