package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.enums.JournalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Document document;

    @Column(nullable = false)
    private LocalDateTime postingDate;

    @Enumerated(EnumType.STRING)
    private JournalStatus status;

    @OneToMany(mappedBy = "journalEntry", fetch = FetchType.LAZY)
    private List<JournalLine> journalLines = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        postingDate = LocalDateTime.now();
    }
}
