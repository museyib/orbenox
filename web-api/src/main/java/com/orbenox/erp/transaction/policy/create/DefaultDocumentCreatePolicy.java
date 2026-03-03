package com.orbenox.erp.transaction.policy.create;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.CommercialContext;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.entity.StockContext;
import com.orbenox.erp.transaction.repository.CommercialContextRepository;
import com.orbenox.erp.transaction.repository.StockContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order
@RequiredArgsConstructor
public class DefaultDocumentCreatePolicy implements DocumentCreatePolicy {
    private final BusinessPartnerRepository businessPartnerRepo;
    private final CommercialContextRepository commercialContextRepo;
    private final PriceListRepository priceListRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockContextRepository stockContextRepository;

    @Override
    public boolean supports(TransactionType type) {
        return true;
    }

    @Override
    public void apply(Document document, CreateDocumentCommand command) {
        TransactionType type = document.getType();

        if (type.isCommercialAffected()) {
            if (command.partnerId() == null) {
                throw new BusinessRuleException("Business Partner is not defined");
            }
            if (command.priceListId() == null) {
                throw new BusinessRuleException("Price List is not defined");
            }

            BusinessPartner businessPartner = businessPartnerRepo.findById(command.partnerId()).orElseThrow();
            PriceList priceList = priceListRepository.findById(command.priceListId()).orElseThrow();

            CommercialContext cc = new CommercialContext();
            cc.setDocument(document);
            cc.setPartner(businessPartner);
            cc.setPriceList(priceList);
            cc.setDueDate(LocalDate.now());
            cc.setPaymentMethod(command.paymentMethod());

            commercialContextRepo.save(cc);
            document.setCommercialContext(cc);
        }

        for (ProductLineCommand lineCommand : command.lines()) {
            Product product = productRepository.getReferenceById(lineCommand.productId());
            ProductLine productLine = new ProductLine();
            productLine.setDocument(document);
            productLine.setProduct(product);
            productLine.setQuantity(lineCommand.quantity());
            productLine.setUnitPrice(lineCommand.unitPrice());
            productLine.setDiscount(lineCommand.discountRatio());
            document.getProductLines().add(productLine);
        }

        if (type.isStockAffected()) {
            setupStockContext(document, command);
        }
    }

    private void setupStockContext(Document document, CreateDocumentCommand command) {
        StockContext sc = new StockContext();
        sc.setDocument(document);

        if (command.sourceWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.getReferenceById(command.sourceWarehouseId());
            sc.setSourceWarehouse(warehouse);
        }
        if (command.targetWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.getReferenceById(command.targetWarehouseId());
            sc.setTargetWarehouse(warehouse);
        }

        document.setStockContext(sc);
        document.getType().getStockAffectDirection().validate(sc);
        stockContextRepository.save(sc);
    }
}
