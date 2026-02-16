package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.CommercialContext;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.entity.StockContext;
import com.orbenox.erp.transaction.repository.CommercialContextRepository;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.repository.StockContextRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepo;
    private final TransactionTypeRepository transactionTypeRepo;
    private final BusinessPartnerRepository businessPartnerRepo;
    private final CommercialContextRepository commercialContextRepo;
    private final PriceListRepository priceListRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockContextRepository stockContextRepository;
    private final LocalizationService i18n;

    @Transactional
    public Document createDocument(CreateDocumentCommand command) {
        TransactionType type = transactionTypeRepo.findById(command.typeId())
                .orElseThrow(() -> new IllegalArgumentException(i18n.msg("error.transactionType.invalid")));

        Document doc = new Document();
        doc.setNumber(command.number());
        doc.setDocumentDate(command.documentDate());
        doc.setType(type);
        doc.setDescription(command.description());
        doc.setDocumentStatus(DocumentStatus.DRAFT);
        doc.setApprovalStatus(ApprovalStatus.AUTO_APPROVED);

        documentRepo.save(doc);

        if (command.partnerId() != null) {
            BusinessPartner businessPartner = businessPartnerRepo.findById(command.partnerId())
                    .orElseThrow();
            PriceList priceList = priceListRepository.findById(command.priceListId())
                    .orElseThrow();

            CommercialContext cc = new CommercialContext();
            cc.setDocument(doc);
            cc.setPartner(businessPartner);
            cc.setPriceList(priceList);
            cc.setDueDate(LocalDate.now());
            cc.setPaymentMethod(command.paymentMethod());

            commercialContextRepo.save(cc);
            doc.setCommercialContext(cc);
        }

        for (ProductLineCommand lineCommand : command.lines()){
            Product product = productRepository.getReferenceById(lineCommand.productId());
            ProductLine productLine = new  ProductLine();
            productLine.setDocument(doc);
            productLine.setProduct(product);
            productLine.setQuantity(lineCommand.quantity());
            productLine.setUnitPrice(lineCommand.unitPrice());
            productLine.setDiscount(lineCommand.discountRatio());
            doc.getProductLines().add(productLine);
        }


        if (command.sourceWarehouseId()  != null || command.targetWarehouseId() != null) {
            StockContext sc = new StockContext();
            sc.setDocument(doc);

            if (command.sourceWarehouseId()  != null) {
                Warehouse warehouse = warehouseRepository.getReferenceById(command.sourceWarehouseId());
                sc.setSourceWarehouse(warehouse);
            }
            if (command.targetWarehouseId()  != null) {
                Warehouse warehouse = warehouseRepository.getReferenceById(command.targetWarehouseId());
                sc.setTargetWarehouse(warehouse);
            }

            stockContextRepository.save(sc);
            doc.setStockContext(sc);
        }

        return doc;
    }
}
