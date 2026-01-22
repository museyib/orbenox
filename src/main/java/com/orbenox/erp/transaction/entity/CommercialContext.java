package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CommercialContext {
    @Id
    @Column(name = "document_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BusinessPartner partner;

    private String paymentMethod;
}
