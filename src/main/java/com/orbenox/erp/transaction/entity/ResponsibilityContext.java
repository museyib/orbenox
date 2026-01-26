package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.security.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ResponsibilityContext {
    @Id
    @Column(name = "document_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser owner;
}
