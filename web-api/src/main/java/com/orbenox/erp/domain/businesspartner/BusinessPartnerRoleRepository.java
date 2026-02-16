package com.orbenox.erp.domain.businesspartner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    List<BusinessPartnerRoleItem> getAllItems();

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
