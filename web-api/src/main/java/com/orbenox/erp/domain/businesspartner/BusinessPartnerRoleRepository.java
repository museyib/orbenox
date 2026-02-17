package com.orbenox.erp.domain.businesspartner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessPartnerRoleRepository extends JpaRepository<BusinessPartnerRole, Long> {
    @Query("""
            SELECT bpr.id as id,
                   bpr.partner.id as partnerId,
                   bpr.partner.code as partnerCode,
                   bpr.partner.name as partnerName,
                   bpr.role as role,
                   bpr.enabled as enabled
            FROM BusinessPartnerRole bpr
            WHERE bpr.deleted = false
            ORDER BY bpr.id""")
    Slice<BusinessPartnerRoleItem> getAllItems(Pageable pageable);

@Query("""
            SELECT bpr.id as id,
                   bpr.partner.id as partnerId,
                   bpr.partner.code as partnerCode,
                   bpr.partner.name as partnerName,
                   bpr.role as role,
                   bpr.enabled as enabled
            FROM BusinessPartnerRole bpr
            WHERE  bpr.deleted = false
                    AND (LOWER(bpr.partner.code) LIKE %:search% OR LOWER(bpr.partner.name) LIKE %:search% OR LOWER(str(bpr.role)) LIKE %:search%)
            ORDER BY bpr.id""")
    Slice<BusinessPartnerRoleItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT bpr.id as id,
                   bpr.partner.id as partnerId,
                   bpr.partner.code as partnerCode,
                   bpr.partner.name as partnerName,
                   bpr.role as role,
                   bpr.enabled as enabled
            FROM BusinessPartnerRole bpr
            WHERE bpr.id = :id AND bpr.deleted = false
            """)
    BusinessPartnerRoleItem getItemById(@Param("id") Long id);

    BusinessPartnerRole findByIdAndDeletedFalse(Long id);
}

