package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.entity.*;
import com.orbenox.erp.domain.stock.StockBalanceRepository;
import com.orbenox.erp.transaction.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            List<StockOperation> operations = new ArrayList<>();
            for (ProductLine line : doc.getProductLines()) {
                if (sc.getSourceWarehouse() != null) {
                    operations.add(new StockOperation(line.getProduct(), sc.getSourceWarehouse(), line.getQuantity(), -1));
                }
                if (sc.getTargetWarehouse() != null) {
                    operations.add(new StockOperation(line.getProduct(), sc.getTargetWarehouse(), line.getQuantity(), 1));
                }
            }

            operations.sort(Comparator
                    .comparing((StockOperation operation) -> operation.warehouse().getId())
                    .thenComparing(operation -> operation.product().getId())
                    .thenComparing(operation -> operation.direction));

            for (StockOperation operation : operations) {
                BigDecimal signedQuantity = operation.quantity()
                        .multiply(BigDecimal.valueOf(operation.direction()));
                createMovement(doc, operation.product(), operation.warehouse(), signedQuantity);
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

        int affected;

        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            affected = stockBalanceRepo.decreaseQuantity(product.getId(), warehouse.getId(), quantity.abs());
        } else if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            affected = stockBalanceRepo.increaseQuantity(product.getId(), warehouse.getId(), quantity);
        } else {
            affected = 0;
        }

        if (affected == 0) {
            throw new BusinessRuleException("Insufficient stock quantity for the product: " + product.getId());
        }
    }

    private record StockOperation(Product product, Warehouse warehouse, BigDecimal quantity, int direction) {
    }
}
