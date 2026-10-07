package com.orbenox.erp.enums;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.entity.StockContext;

public enum StockAffectDirection {
    IN {
        @Override
        public void validate(StockContext stockContext) {
            if (stockContext.getTargetWarehouse() == null) {
                throw new BusinessRuleException("Target Warehouse is not defined");
            } else if (stockContext.getSourceWarehouse() != null) {
                throw new BusinessRuleException("Source Warehouse cannot be applied");
            }
        }
    },
    OUT {
        @Override
        public void validate(StockContext stockContext) {
            if (stockContext.getSourceWarehouse() == null) {
                throw new BusinessRuleException("Source Warehouse is not defined");
            } else if (stockContext.getTargetWarehouse() != null) {
                throw new BusinessRuleException("Target Warehouse cannot be applied for this document");
            }
        }
    },
    IN_OUT {
        @Override
        public void validate(StockContext stockContext) {
            if (stockContext.getTargetWarehouse() == null ||
                    stockContext.getSourceWarehouse() == null) {
                throw new BusinessRuleException("Both Warehouse must be defined");
            }
        }
    };

    public abstract void validate(StockContext stockContext);
}