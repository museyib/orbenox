package com.orbenox.erp.domain;

import com.orbenox.erp.domain.account.*;
import com.orbenox.erp.domain.businesspartner.*;
import com.orbenox.erp.domain.country.*;
import com.orbenox.erp.domain.currency.*;
import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.domain.postingrule.PostingRuleDto;
import com.orbenox.erp.domain.postingrule.PostingRuleMapper;
import com.orbenox.erp.domain.postingrule.PostingRuleMapperImpl;
import com.orbenox.erp.domain.resource.*;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimension;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionDto;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionMapper;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionMapperImpl;
import com.orbenox.erp.domain.warehouse.*;
import com.orbenox.erp.enums.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainMappersTest {
    private final AccountMapper accounts = new AccountMapperImpl();
    private final BusinessPartnerMapper partners = new BusinessPartnerMapperImpl();
    private final BusinessPartnerRoleMapper partnerRoles = new BusinessPartnerRoleMapperImpl();
    private final CountryMapper countries = new CountryMapperImpl();
    private final CurrencyMapper currencies = new CurrencyMapperImpl();
    private final PostingRuleMapper postingRules = new PostingRuleMapperImpl();
    private final ResourceMapper resources = new ResourceMapperImpl();
    private final UnitDimensionMapper unitDimensions = new UnitDimensionMapperImpl();
    private final WarehouseMapper warehouses = new WarehouseMapperImpl();

    @Test
    void cardAndBusinessPartnerMappersCopyTheirFields() {
        Account account = accounts.toEntity(new AccountCreateDto(true, "100", "Cash", AccountType.ASSET));
        BusinessPartner partner = partners.toEntity(
                new BusinessPartnerCreateDto(false, "BP1", "Partner", "TAX-9", PartnerType.COMPANY));
        Country country = countries.toEntity(new CountryCreateDto(true, "US", "United States"));
        Currency currency = currencies.toEntity(new CurrencyCreateDto(false, "USD", "US Dollar"));
        UnitDimension dimension = unitDimensions.toEntity(new UnitDimensionDto(null, true, "MASS", "Mass"));
        Warehouse warehouse = warehouses.toEntity(new WarehouseCreateDto(true, "WH1", "Main"));

        assertThat(account.isEnabled()).isTrue();
        assertThat(account.getCode()).isEqualTo("100");
        assertThat(account.getName()).isEqualTo("Cash");
        assertThat(account.getAccountType()).isEqualTo(AccountType.ASSET);
        assertThat(partner.isEnabled()).isFalse();
        assertThat(partner.getCode()).isEqualTo("BP1");
        assertThat(partner.getName()).isEqualTo("Partner");
        assertThat(partner.getTaxId()).isEqualTo("TAX-9");
        assertThat(partner.getType()).isEqualTo(PartnerType.COMPANY);
        assertThat(country.isEnabled()).isTrue();
        assertThat(country.getCode()).isEqualTo("US");
        assertThat(country.getName()).isEqualTo("United States");
        assertThat(currency.isEnabled()).isFalse();
        assertThat(currency.getCode()).isEqualTo("USD");
        assertThat(currency.getName()).isEqualTo("US Dollar");
        assertThat(dimension.isEnabled()).isTrue();
        assertThat(dimension.getCode()).isEqualTo("MASS");
        assertThat(dimension.getName()).isEqualTo("Mass");
        assertThat(warehouse.isEnabled()).isTrue();
        assertThat(warehouse.getCode()).isEqualTo("WH1");
        assertThat(warehouse.getName()).isEqualTo("Main");
    }

    @Test
    void partnerRoleMapperMapsRoleButDoesNotSetPartnerFromDto() {
        BusinessPartnerRole role = partnerRoles.toEntity(
                new BusinessPartnerRoleCreateDto(true, 99L, PartnerRole.CUSTOMER));

        assertThat(role.isEnabled()).isTrue();
        assertThat(role.getRole()).isEqualTo(PartnerRole.CUSTOMER);
        assertThat(role.getPartner()).isNull();
        role.setPartner(new BusinessPartner());
        partnerRoles.updateEntityFromDto(
                new BusinessPartnerRoleUpdateDto(5L, false, 100L, PartnerRole.SUPPLIER), role);
        assertThat(role.getId()).isEqualTo(5L);
        assertThat(role.isEnabled()).isFalse();
        assertThat(role.getRole()).isEqualTo(PartnerRole.SUPPLIER);
        assertThat(role.getPartner()).isNotNull();
    }

    @Test
    void resourceMapperConvertsActionNamesAndLeavesActionsAloneDuringUpdate() {
        Resource resource = resources.toEntity(new ResourceCreateDto(true, "DOC", "Document",
                java.util.Set.of("READ", "APPROVE")));

        assertThat(resource.isEnabled()).isTrue();
        assertThat(resource.getCode()).isEqualTo("DOC");
        assertThat(resource.getName()).isEqualTo("Document");
        assertThat(resource.getActions()).containsExactlyInAnyOrder(Action.READ, Action.APPROVE);

        resources.updateEntityFromDto(new ResourceUpdateDto(6L, false, "DOC2", "Updated",
                java.util.Set.of("DELETE")), resource);
        assertThat(resource.getId()).isEqualTo(6L);
        assertThat(resource.isEnabled()).isFalse();
        assertThat(resource.getCode()).isEqualTo("DOC2");
        assertThat(resource.getName()).isEqualTo("Updated");
        assertThat(resource.getActions()).containsExactlyInAnyOrder(Action.READ, Action.APPROVE);
    }

    @Test
    void postingRuleMapperCopiesScalarRuleFieldsButIgnoresRelations() {
        PostingRule rule = postingRules.toEntity(
                new PostingRuleDto(7, 10L, 20L, 30L, AmountSource.TOTAL, PartnerSide.CREDIT));

        assertThat(rule.getSequence()).isEqualTo(7);
        assertThat(rule.getAmountSource()).isEqualTo(AmountSource.TOTAL);
        assertThat(rule.getPartnerSide()).isEqualTo(PartnerSide.CREDIT);
        assertThat(rule.getType()).isNull();
        assertThat(rule.getDebitAccount()).isNull();
        assertThat(rule.getCreditAccount()).isNull();
    }

    @Test
    void updateMappersReplaceSpecifiedValuesAndRetainExistingNullableValues() {
        Account account = new Account();
        account.setName("old account");
        accounts.updateEntityFromDto(new AccountUpdateDto(1L, true, "200", "New account", AccountType.EXPENSE), account);
        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getName()).isEqualTo("New account");
        assertThat(account.getAccountType()).isEqualTo(AccountType.EXPENSE);

        BusinessPartner partner = new BusinessPartner();
        partners.updateEntityFromDto(
                new BusinessPartnerUpdateDto(2L, true, "BP2", "New partner", "TAX-10", PartnerType.PERSON), partner);
        assertThat(partner.getId()).isEqualTo(2L);
        assertThat(partner.getTaxId()).isEqualTo("TAX-10");
        assertThat(partner.getType()).isEqualTo(PartnerType.PERSON);

        Warehouse warehouse = new Warehouse();
        warehouse.setName("old warehouse");
        warehouses.updateEntityFromDto(new WarehouseUpdateDto(3L, false, "WH2", null), warehouse);
        assertThat(warehouse.getId()).isEqualTo(3L);
        assertThat(warehouse.getCode()).isEqualTo("WH2");
        assertThat(warehouse.getName()).isEqualTo("old warehouse");

        Country country = new Country();
        countries.updateEntityFromDto(new CountryUpdateDto(4L, false, "CA", "Canada"), country);
        assertThat(country.getId()).isEqualTo(4L);
        assertThat(country.getCode()).isEqualTo("CA");
        assertThat(country.getName()).isEqualTo("Canada");

        Currency currency = new Currency();
        currencies.updateEntityFromDto(new CurrencyUpdateDto(5L, true, "EUR", "Euro"), currency);
        assertThat(currency.getId()).isEqualTo(5L);
        assertThat(currency.getCode()).isEqualTo("EUR");
        assertThat(currency.getName()).isEqualTo("Euro");
    }
}
