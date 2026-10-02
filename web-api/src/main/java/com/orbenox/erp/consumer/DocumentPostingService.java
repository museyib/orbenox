package com.orbenox.erp.consumer;

import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.DocumentResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
class DocumentPostingService {
    private final DocumentResolver documentResolver;

    void updateStatus(Long documentId, String typeCode, DocumentStatus status) {
        Optional<Document> doc = documentResolver.resolveOptional(documentId, typeCode);

        doc.ifPresent(d -> d.setDocumentStatus(status));
    }
}
