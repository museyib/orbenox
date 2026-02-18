package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.stock.StockBalance;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.transaction.entity.*;
import com.orbenox.erp.domain.stock.StockBalanceRepository;
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
    private final StockBalanceRepository stockBalanceRepo;

    @Override
    public void post(Document doc) {
        StockContext sc = doc.getStockContext();

        if (sc != null) {
            for (ProductLine line : doc.getProductLines()) {
                if (sc.getSourceWarehouse() != null)
                    createMovement(doc, line.getProduct(), sc.getSourceWarehouse(), line.getQuantity().negate());
                if (sc.getTargetWarehouse() != null)
                    createMovement(doc, line.getProduct(), sc.getTargetWarehouse(), line.getQuantity());
            }
        }
    }

    private void createMovement(Document doc, Product product, Warehouse warehouse, BigDecimal quantity) {
        StockMovement sm = new StockMovement();
        sm.setProduct(product);
        sm.setWarehouse(warehouse);
        sm.setQuantity(quantity);
        sm.setDocument(doc);
        stockMovementRepo.save(sm);

        applyMovement(product, warehouse, quantity);
    }

    private void applyMovement(Product product, Warehouse warehouse, BigDecimal quantity) {
        StockBalance stockBalance = stockBalanceRepo.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    StockBalance sb = new StockBalance();
                    sb.setProduct(product);
                    sb.setWarehouse(warehouse);
                    sb.setQuantity(BigDecimal.ZERO);
                    return sb;
                });
        stockBalance.setQuantity(stockBalance.getQuantity().add(quantity));
        stockBalanceRepo.save(stockBalance);
    }
}
