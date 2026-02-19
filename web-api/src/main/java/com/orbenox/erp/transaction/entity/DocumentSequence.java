package com.orbenox.erp.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "documentTypeCode",
                        "year",
                        "month"
                })
        }
)
public class DocumentSequence {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String documentTypeCode;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private long nextValue;
}