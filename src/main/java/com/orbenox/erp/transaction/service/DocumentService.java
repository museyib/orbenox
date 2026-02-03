package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.CommercialContext;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.CommercialContextRepository;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepo;
    private final TransactionTypeRepository transactionTypeRepo;
    private final BusinessPartnerRepository businessPartnerRepo;
    private final CommercialContextRepository commercialContextRepo;

    public Document createDocument(CreateDocumentCommand command) {
        TransactionType type = transactionTypeRepo.findById(command.typeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction type"));

        Document doc = new Document();
        doc.setNumber(command.number());
        doc.setDocumentDate(command.date());
        doc.setType(type);
        doc.setDescription(command.description());
        doc.setDocumentStatus(DocumentStatus.DRAFT);
        doc.setApprovalStatus(ApprovalStatus.AUTO_APPROVED);

        documentRepo.save(doc);

        if (command.partnerId() != null) {
            BusinessPartner businessPartner = businessPartnerRepo.findById(command.partnerId())
                    .orElseThrow();

            CommercialContext cc = new CommercialContext();
            cc.setDocument(doc);
            cc.setPartner(businessPartner);
            cc.setPaymentMethod(command.paymentMethod());

            commercialContextRepo.save(cc);
            doc.setCommercialContext(cc);
        }

        return doc;
    }
}
