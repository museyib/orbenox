package com.orbenox.erp.transaction.service;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentResolver {

    private final DocumentRepository documentRepo;
    private final LocalizationService i18n;

    public Document resolve(Long documentId, String documentType) {
        return documentRepo.findByIdAndTypeCode(documentId, documentType)
                .orElseThrow(() -> new BusinessRuleException(i18n.msg("error.document.invalidIdForSpecifiedType", documentId)));
    }

    public Optional<Document> resolveOptional(Long documentId, String documentType) {
        return documentRepo.findByIdAndTypeCode(documentId, documentType);
    }
}
