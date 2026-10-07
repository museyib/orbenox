package com.orbenox.erp.enums;

import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.entity.StockContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockAffectDirectionTest {

    @Test
    void validateIn_shouldRequireOnlyTargetWarehouse() {
        StockContext stockContext = context(null, new Warehouse());

        assertThatNoException().isThrownBy(() -> StockAffectDirection.IN.validate(stockContext));
        assertThatThrownBy(() -> StockAffectDirection.IN.validate(context(null, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Target Warehouse is not defined");
        assertThatThrownBy(() -> StockAffectDirection.IN.validate(context(new Warehouse(), new Warehouse())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Source Warehouse cannot be applied");
    }

    @Test
    void validateOut_shouldRequireOnlySourceWarehouse() {
        StockContext stockContext = context(new Warehouse(), null);

        assertThatNoException().isThrownBy(() -> StockAffectDirection.OUT.validate(stockContext));
        assertThatThrownBy(() -> StockAffectDirection.OUT.validate(context(null, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Source Warehouse is not defined");
        assertThatThrownBy(() -> StockAffectDirection.OUT.validate(context(new Warehouse(), new Warehouse())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Target Warehouse cannot be applied for this document");
    }

    @Test
    void validateInOut_shouldRequireBothWarehouses() {
        assertThatNoException().isThrownBy(() ->
                StockAffectDirection.IN_OUT.validate(context(new Warehouse(), new Warehouse())));
        assertThatThrownBy(() -> StockAffectDirection.IN_OUT.validate(context(null, new Warehouse())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Both Warehouse must be defined");
        assertThatThrownBy(() -> StockAffectDirection.IN_OUT.validate(context(new Warehouse(), null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Both Warehouse must be defined");
    }

    private StockContext context(Warehouse source, Warehouse target) {
        StockContext stockContext = new StockContext();
        stockContext.setSourceWarehouse(source);
        stockContext.setTargetWarehouse(target);
        return stockContext;
    }
}
