package com.orbenox.erp.product.service;

import com.orbenox.erp.common.country.CountryRepository;
import com.orbenox.erp.product.dto.ProductProperties;
import com.orbenox.erp.product.repository.*;
import com.orbenox.erp.unit.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductPropertyService {
    private final BrandRepository brandRepository;
    private final ProducerRepository producerRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductClassRepository productClassRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductGroupRepository productGroupRepository;
    private final UnitRepository unitRepository;
    private final CountryRepository countryRepository;

    public ProductProperties getProductProperties() {
        ProductProperties productProperties = new ProductProperties();
        productProperties.setBrands(brandRepository.getAllItems());
        productProperties.setCountries(countryRepository.getAllItems());
        productProperties.setProductCategories(productCategoryRepository.getAllItems());
        productProperties.setProductTypes(productTypeRepository.getAllItems());
        productProperties.setProductClasses(productClassRepository.getAllItems());
        productProperties.setProductGroups(productGroupRepository.getAllItems());
        productProperties.setUnits(unitRepository.getSimpleItems());
        productProperties.setProducers(producerRepository.getAllItems());
        return productProperties;
    }
}
