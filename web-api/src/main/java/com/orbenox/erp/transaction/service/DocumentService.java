package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.domain.transactiontype.numbering.DocumentNumberGenerator;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.policy.create.DocumentCreatePolicy;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentNumberGenerator numberGenerator;
    private final DocumentRepository documentRepo;
    private final TransactionTypeRepository transactionTypeRepo;
    private final PolicyResolver<DocumentCreatePolicy> documentCreatePolicyResolver;
    private final LocalizationService i18n;

    @Transactional
    public Document createDocument(CreateDocumentCommand command) {
        TransactionType type = transactionTypeRepo.findById(command.typeId())
                .orElseThrow(() -> new IllegalArgumentException(i18n.msg("error.transactionType.invalid")));

        Document doc = new Document();
        doc.setDocumentNo(numberGenerator.generate(type, command.documentDate()));
        doc.setDocumentDate(command.documentDate());
        doc.setType(type);
        doc.setDescription(command.description());
        doc.setDocumentStatus(DocumentStatus.DRAFT);
        doc.setApprovalStatus(ApprovalStatus.AUTO_APPROVED);

        documentRepo.save(doc);
        documentCreatePolicyResolver.resolve(type).apply(doc, command);

        return doc;
    }
}
