package com.orbenox.erp.stockservice.service;

import com.orbenox.erp.common.idempotency.Idempotent;
import com.orbenox.erp.stockservice.entity.Document;
import com.orbenox.erp.stockservice.entity.ProductLine;
import com.orbenox.erp.stockservice.entity.StockContext;
import com.orbenox.erp.stockservice.entity.StockMovement;
import com.orbenox.erp.stockservice.exception.BusinessRuleException;
import com.orbenox.erp.stockservice.repository.StockBalanceRepository;
import com.orbenox.erp.stockservice.repository.StockMovementRepository;
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
public class StockService {

    private final StockMovementRepository stockMovementRepo;
    private final StockBalanceRepository stockBalanceRepo;

    @Idempotent
    public void post(Document doc) {
        StockContext sc = doc.getStockContext();

        if (sc != null) {
            List<StockOperation> operations = new ArrayList<>();
            for (ProductLine line : doc.getProductLines()) {
                if (sc.getSourceWarehouseId() != null) {
                    operations.add(new StockOperation(line.getProductId(), sc.getSourceWarehouseId(), line.getQuantity(), -1));
                }
                if (sc.getTargetWarehouseId() != null) {
                    operations.add(new StockOperation(line.getProductId(), sc.getTargetWarehouseId(), line.getQuantity(), 1));
                }
            }

            operations.sort(Comparator
                    .comparing((StockOperation operation) -> operation.warehouseId)
                    .thenComparing(operation -> operation.productId)
                    .thenComparing(operation -> operation.direction));

            for (StockOperation operation : operations) {
                BigDecimal signedQuantity = operation.quantity()
                        .multiply(BigDecimal.valueOf(operation.direction()));
                createMovement(doc, operation.productId, operation.warehouseId, signedQuantity);
            }
        }
    }

    private void createMovement(Document doc, Long productId, Long warehouseId, BigDecimal quantity) {
        StockMovement sm = new StockMovement();
        sm.setProductId(productId);
        sm.setWarehouseId(warehouseId);
        sm.setQuantity(quantity);
        sm.setDocumentId(doc.getId());
        stockMovementRepo.save(sm);

        applyMovement(productId, warehouseId, quantity);
    }

    private void applyMovement(Long productId, Long warehouseId, BigDecimal quantity) {

        int affected;

        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            affected = stockBalanceRepo.decreaseQuantity(productId, warehouseId, quantity.abs());
        } else if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            affected = stockBalanceRepo.increaseQuantity(productId, warehouseId, quantity);
        } else {
            affected = 0;
        }

        if (affected == 0) {
            throw new BusinessRuleException("Insufficient stock quantity for the product: " + productId);
        }
    }

    private record StockOperation(Long productId, Long warehouseId, BigDecimal quantity, int direction) {
    }
}
