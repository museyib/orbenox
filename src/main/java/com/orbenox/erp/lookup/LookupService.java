package com.orbenox.erp.lookup;

import com.orbenox.erp.common.action.ActionRepository;
import com.orbenox.erp.common.country.CountryRepository;
import com.orbenox.erp.currency.CurrencyRepository;
import com.orbenox.erp.product.repository.*;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserTypeRepository;
import com.orbenox.erp.unit.UnitRepository;
import com.orbenox.erp.unit.unitdimension.UnitDimensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ActionRepository actionRepository;

    @Cacheable("lookups")
    public Map<String, Object> getLookups(List<String> types) {
        Map<String, Object> result = new HashMap<>();
        for (String type : types) {
            switch (type) {
                case "brands" -> result.put("brands", brandRepository.getAllItems());
                case "productTypes" -> result.put("productTypes", productTypeRepository.getAllItems());
                case "productClasses" -> result.put("productClasses", productClassRepository.getAllItems());
                case "productGroups" -> result.put("productGroups", productGroupRepository.getSimpleItems());
                case "units" -> result.put("units", unitRepository.getSimpleItems());
                case "countries" -> result.put("countries", countryRepository.getAllItems());
                case "productCategories" -> result.put("productCategories", productCategoryRepository.getAllItems());
                case "producers" -> result.put("producers", producerRepository.getAllItems());
                case "userTypes" -> result.put("userTypes", userTypeRepository.getAllItems());
                case "products" -> result.put("products", productRepository.getAllItems());
                case "unitDimensions" -> result.put("unitDimensions", unitDimensionRepository.getAllItems());
                case "currencies" -> result.put("currencies", currencyRepository.getAllItems());
                case "roles" -> result.put("roles", roleRepository.getAllItems());
                case "actions" -> result.put("actions", actionRepository.getAllItems());
            }
        }
        return result;
    }
}
