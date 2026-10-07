package com.orbenox.erp.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class EnumServiceTest {

    private final EnumService enumService = new EnumService();

    @Test
    void getActions_shouldReturnActionNames() {
        assertThat(enumService.getActions()).containsExactly(names(Action.values()));
    }

    @Test
    void getAccountTypes_shouldReturnAccountTypeNames() {
        assertThat(enumService.getAccountTypes()).containsExactly(names(AccountType.values()));
    }

    @Test
    void getAmountSources_shouldReturnAmountSourceNames() {
        assertThat(enumService.getAmountSources()).containsExactly(names(AmountSource.values()));
    }

    @Test
    void getApprovalStatuses_shouldReturnApprovalStatusNames() {
        assertThat(enumService.getApprovalStatuses()).containsExactly(names(ApprovalStatus.values()));
    }

    @Test
    void getDocumentStatuses_shouldReturnDocumentStatusNames() {
        assertThat(enumService.getDocumentStatuses()).containsExactly(names(DocumentStatus.values()));
    }

    @Test
    void getPartnerRoles_shouldReturnPartnerRoleNames() {
        assertThat(enumService.getPartnerRoles()).containsExactly(names(PartnerRole.values()));
    }

    @Test
    void getPartnerSides_shouldReturnPartnerSideNames() {
        assertThat(enumService.getPartnerSides()).containsExactly(names(PartnerSide.values()));
    }

    @Test
    void getPartnerTypes_shouldReturnPartnerTypeNames() {
        assertThat(enumService.getPartnerTypes()).containsExactly(names(PartnerType.values()));
    }

    @Test
    void getResetPeriods_shouldReturnResetPeriodNames() {
        assertThat(enumService.getResetPeriods()).containsExactly(names(ResetPeriod.values()));
    }

    @Test
    void getStockAffectDirections_shouldReturnStockAffectDirectionNames() {
        assertThat(enumService.getStockAffectDirections()).containsExactly(names(StockAffectDirection.values()));
    }

    private String[] names(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::name).toArray(String[]::new);
    }
}
