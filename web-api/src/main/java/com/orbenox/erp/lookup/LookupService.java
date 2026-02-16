package com.orbenox.erp.lookup;

import com.orbenox.erp.domain.country.CountryRepository;
import com.orbenox.erp.domain.currency.CurrencyRepository;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.repository.*;
import com.orbenox.erp.domain.account.AccountRepository;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.unit.UnitRepository;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionRepository;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.enums.EnumService;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;

@Service
@RequiredArgsConstructor
public class LookupService {
    private final BrandRepository brandRepository;
    private final ProducerRepository producerRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductClassRepository productClassRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductGroupRepository productGroupRepository;
    private final UnitRepository unitRepository;
    private final CountryRepository countryRepository;
    private final UserTypeRepository userTypeRepository;
    private final ProductRepository productRepository;
    private final UnitDimensionRepository unitDimensionRepository;
    private final CurrencyRepository currencyRepository;
    private final RoleRepository roleRepository;
    private final PriceListRepository priceListRepository;
    private final WarehouseRepository warehouseRepository;
    private final AccountRepository accountRepository;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final EnumService enumService;

    @Cacheable(LOOKUPS)
    public Map<String, Object> getLookups(List<String> types) {
        Map<String, Object> result = new HashMap<>();
        for (String type : types) {
            switch (type) {
                case "brands" -> result.put("brands", brandRepository.getEnabledItems());
                case "productTypes" -> result.put("productTypes", productTypeRepository.getEnabledItems());
                case "productClasses" -> result.put("productClasses", productClassRepository.getEnabledItems());
                case "productGroups" -> result.put("productGroups", productGroupRepository.getEnabledItems());
                case "units" -> result.put("units", unitRepository.getEnabledItems());
                case "countries" -> result.put("countries", countryRepository.getEnabledItems());
                case "productCategories" -> result.put("productCategories", productCategoryRepository.getEnabledItems());
                case "producers" -> result.put("producers", producerRepository.getEnabledItems());
                case "userTypes" -> result.put("userTypes", userTypeRepository.getEnabledItems());
                case "products" -> result.put("products", productRepository.getEnabledItems());
                case "unitDimensions" -> result.put("unitDimensions", unitDimensionRepository.getEnabledItems());
                case "currencies" -> result.put("currencies", currencyRepository.getEnabledItems());
                case "roles" -> result.put("roles", roleRepository.getEnabledItems());
                case "priceLists" -> result.put("priceLists", priceListRepository.getEnabledItems());
                case "warehouses" -> result.put("warehouses", warehouseRepository.getEnabledItems());
                case "accounts" -> result.put("accounts", accountRepository.getEnabledItems());
                case "businessPartners" -> result.put("businessPartners", businessPartnerRepository.getEnabledItems());
                case "transactionTypes" -> result.put("transactionTypes", transactionTypeRepository.getEnabledItems());
                case "actions" -> result.put("actions", enumService.getActions());
                case "accountTypes" -> result.put("accountTypes", enumService.getAccountTypes());
                case "amountSources" -> result.put("amountSources", enumService.getAmountSources());
                case "approvalStatuses" -> result.put("approvalStatuses", enumService.getApprovalStatuses());
                case "documentStatuses" -> result.put("documentStatuses", enumService.getDocumentStatuses());
                case "partnerSides" -> result.put("partnerSides", enumService.getPartnerSides());
                case "partnerRoles" -> result.put("partnerRoles", enumService.getPartnerRoles());
                case "partnerTypes" -> result.put("partnerTypes", enumService.getPartnerTypes());
            }
        }
        return result;
    }
}

