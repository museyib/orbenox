package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.entity.StockContext;
import com.orbenox.erp.transaction.entity.StockMovement;
import com.orbenox.erp.transaction.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService implements ContextService {

    private final StockMovementRepository stockMovementRepo;

    @Override
    public void post(Document doc) {
        StockContext sc = doc.getStockContext();

        for (ProductLine line: doc.getProductLines()) {
            if (sc.getSourceWarehouse() != null)
                createMovement(doc, line.getProduct(), sc.getSourceWarehouse(), line.getQuantity().negate());
            if (sc.getTargetWarehouse() != null)
                createMovement(doc, line.getProduct(), sc.getTargetWarehouse(), line.getQuantity());
        }
    }

    private void createMovement(Document doc, Product product, Warehouse warehouse, BigDecimal quantity) {
        StockMovement sm = new StockMovement();
        sm.setProduct(product);
        sm.setWarehouse(warehouse);
        sm.setQuantity(quantity);
        sm.setDocument(doc);
        stockMovementRepo.save(sm);
    }
}
