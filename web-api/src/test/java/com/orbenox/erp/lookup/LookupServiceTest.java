package com.orbenox.erp.lookup;

import com.orbenox.erp.domain.account.AccountRepository;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.country.CountryRepository;
import com.orbenox.erp.domain.currency.CurrencyRepository;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.projection.BrandItem;
import com.orbenox.erp.domain.product.repository.*;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.unit.UnitRepository;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionRepository;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.enums.EnumService;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LookupServiceTest {
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ProducerRepository producerRepository;
    @Mock
    private ProductTypeRepository productTypeRepository;
    @Mock
    private ProductClassRepository productClassRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductGroupRepository productGroupRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserTypeRepository userTypeRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UnitDimensionRepository unitDimensionRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PriceListRepository priceListRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BusinessPartnerRepository businessPartnerRepository;
    @Mock
    private TransactionTypeRepository transactionTypeRepository;
    @Mock
    private EnumService enumService;

    @InjectMocks
    private LookupService service;

    @Test
    void getLookups_shouldLoadRequestedRepositoryAndEnumLookupsOnly() {
        List<BrandItem> enabledBrands = List.of(mock(BrandItem.class));
        List<String> actions = List.of("CREATE", "POST");
        List<String> approvalStatuses = List.of("PENDING", "APPROVED");
        when(brandRepository.getEnabledItems()).thenReturn(enabledBrands);
        when(enumService.getActions()).thenReturn(actions);
        when(enumService.getApprovalStatuses()).thenReturn(approvalStatuses);

        Map<String, Object> result = service.getLookups(
                List.of("brands", "actions", "approvalStatuses", "unsupported", "brands"));

        assertThat(result).containsEntry("brands", enabledBrands)
                .containsEntry("actions", actions)
                .containsEntry("approvalStatuses", approvalStatuses)
                .doesNotContainKey("unsupported")
                .hasSize(3);
        verify(brandRepository, times(2)).getEnabledItems();
        verify(enumService).getActions();
        verify(enumService).getApprovalStatuses();
        verifyNoInteractions(producerRepository, productTypeRepository, productClassRepository,
                productCategoryRepository, productGroupRepository, unitRepository, countryRepository,
                userTypeRepository, productRepository, unitDimensionRepository, currencyRepository,
                roleRepository, priceListRepository, warehouseRepository, accountRepository,
                businessPartnerRepository, transactionTypeRepository);
    }

    @Test
    void getLookups_shouldReturnEmptyMapForEmptyRequest() {
        assertThat(service.getLookups(List.of())).isEmpty();
        verifyNoInteractions(brandRepository, enumService);
    }
}
