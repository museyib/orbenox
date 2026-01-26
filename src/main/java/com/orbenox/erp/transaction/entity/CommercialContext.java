package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.domain.currency.Currency;
import com.orbenox.erp.domain.price.PriceList;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private PriceList priceList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Currency currency;

    private String paymentMethod;
}
