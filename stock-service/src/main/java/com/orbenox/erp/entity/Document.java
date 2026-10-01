package com.orbenox.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String documentNo;

    @Column(nullable = false)
    private LocalDate documentDate;

    private String description;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private StockContext stockContext;

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<ProductLine> productLines = new ArrayList<>();
}
