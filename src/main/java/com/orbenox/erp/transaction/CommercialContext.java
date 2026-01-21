package com.orbenox.erp.transaction;

import com.orbenox.erp.domain.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CommercialContext {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Customer customer;

    private String paymentMethod;
}
