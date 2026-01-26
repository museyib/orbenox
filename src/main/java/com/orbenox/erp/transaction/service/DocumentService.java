package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductWarehouseRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.transaction.command.ApproveProductCommand;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.CommercialContext;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.repository.CommercialContextRepository;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.repository.ProductLineRepository;
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
    private final WarehouseRepository warehouseRepo;
    private final ProductRepository productRepo;
    private final ProductLineRepository productLineRepo;
    private final ProductWarehouseRepository productWarehouseRepo;

    public Document createDocument(CreateDocumentCommand command) {
        TransactionType type = transactionTypeRepo.findById(command.typeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction type"));

        Document doc = new Document();
        doc.setNumber(command.number());
        doc.setDate(command.date());
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

    public Document approveProduct(ApproveProductCommand cmd) {
        TransactionType type = transactionTypeRepo.findByIdAndDeletedFalse(cmd.typeId());

        Warehouse warehouse = warehouseRepo.findByIdAndDeletedFalse(cmd.warehouseId());

        Document doc = new Document();
        doc.setNumber(cmd.number());
        doc.setDate(cmd.date());
        doc.setType(type);
        doc.setDescription(cmd.description());

        documentRepo.save(doc);

        for (ProductLineCommand lineCommand : cmd.lines()) {
            Product product = productRepo.findByIdAndDeletedFalse(lineCommand.productId());
            ProductLine productLine = new ProductLine();
            productLine.setProduct(product);
            productLine.setDocument(doc);
            productLine.setQuantity(lineCommand.quantity());
            productLineRepo.save(productLine);

            ProductWarehouse productWarehouse = productWarehouseRepo.findByProductAndWarehouse(product, warehouse)
                    .orElseGet(() -> new ProductWarehouse(product, warehouse));
            productWarehouse.setQuantity(lineCommand.quantity());
            productWarehouseRepo.save(productWarehouse);
        }

        doc.setApprovalStatus(ApprovalStatus.APPROVED);
        doc.setDocumentStatus(DocumentStatus.POSTED);

        return doc;
    }
}
