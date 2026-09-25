package com.orbenox.erp.transaction.service;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentResolver {

    private final DocumentRepository documentRepo;
    private final LocalizationService i18n;

    public Document resolve(Long documentId, String documentType) {
        Document doc = documentRepo.findById(documentId).orElseThrow();

        if (!doc.getType().getCode().equals(documentType))
            throw new BusinessRuleException(i18n.msg("error.document.invalidIdForSalesOrderType", documentId));

        return doc;
    }
}
