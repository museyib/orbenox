package com.orbenox.erp.service;

import com.orbenox.erp.entity.StockMovement;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.repository.StockBalanceRepository;
import com.orbenox.erp.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMovementRepository stockMovementRepo;
    private final StockBalanceRepository stockBalanceRepo;

    public void updateStock(StockMovementCommand command) {

        Long documentId = command.documentId();
        List<StockOperation> operations = new ArrayList<>(command.operations());

        operations.sort(Comparator
                .comparing(StockOperation::warehouseId)
                .thenComparing(StockOperation::productId)
                .thenComparing(StockOperation::direction));

        for (StockOperation operation : operations) {
            createMovement(documentId, operation);
        }
    }

    private void createMovement(Long documentId, StockOperation operation) {
        BigDecimal signedQuantity = operation.quantity()
                .multiply(BigDecimal.valueOf(operation.direction()));
        StockMovement sm = new StockMovement();
        sm.setProductId(operation.productId());
        sm.setWarehouseId(operation.warehouseId());
        sm.setQuantity(signedQuantity);
        sm.setDocumentId(documentId);
        sm.setLineId(operation.lineId());
        stockMovementRepo.save(sm);

        applyMovement(operation.productId(), operation.warehouseId(), signedQuantity);
    }

    private void applyMovement(Long productId, Long warehouseId, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            if (stockBalanceRepo.decreaseQuantity(productId, warehouseId, quantity.abs()) == 0) {
                throw new BusinessRuleException("Insufficient stock quantity for the product: " + productId);
            }
        } else if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            if (stockBalanceRepo.increaseQuantity(productId, warehouseId, quantity.abs()) == 0) {
                throw new BusinessRuleException("Something went wrong for the product: " + productId);
            }
        }
    }
}
