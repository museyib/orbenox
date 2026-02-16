package com.orbenox.erp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine for better performance
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                CacheNames.USER_DETAILS,
                CacheNames.HAS_PERMISSION,
                CacheNames.USERS,
                CacheNames.ROLES,
                CacheNames.RESOURCES,
                CacheNames.ACTIONS,
                CacheNames.PRODUCTS,
                CacheNames.PRODUCT_BARCODES,
                CacheNames.BRANDS,
                CacheNames.PRODUCERS,
                CacheNames.PRODUCT_CATEGORIES,
                CacheNames.PRODUCT_CLASSES,
                CacheNames.PRODUCT_TYPES,
                CacheNames.PRODUCT_GROUPS_ALL,
                CacheNames.PRODUCT_GROUPS_EXCLUDED,
                CacheNames.UNITS_ALL,
                CacheNames.UNITS_BY_DIMENSION_ID,
                CacheNames.CURRENCIES,
                CacheNames.COUNTRIES,
                CacheNames.WAREHOUSES,
                CacheNames.PRICE_LISTS_ALL,
                CacheNames.PRICE_LISTS_EXCLUDED,
                CacheNames.LOOKUPS,
                CacheNames.PERMISSIONS,
                CacheNames.TRANSACTION_TYPES,
                CacheNames.ACCOUNT_TYPES,
                CacheNames.APPROVAL_STATUSES,
                CacheNames.DOCUMENT_STATUSES,
                CacheNames.PARTNER_ROLES,
                CacheNames.PARTNER_TYPES,
                CacheNames.PERMISSIONS_USER,
                CacheNames.PERMISSIONS_ROLE,
                CacheNames.AVAILABLE_RESOURCE_ACTIONS_USER,
                CacheNames.AVAILABLE_RESOURCE_ACTIONS_ROLE,
                CacheNames.PRODUCT_PRICES,
                CacheNames.PRODUCT_UNITS,
                CacheNames.PRODUCT_WAREHOUSES
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats(); // Enable cache statistics for monitoring
    }

    /**
     * Cache names constants for reference
     */
    public static class CacheNames {
        public static final String USER_DETAILS = "userDetails";
        public static final String HAS_PERMISSION = "hasPermission";
        public static final String USERS = "users";
        public static final String ROLES = "roles";
        public static final String RESOURCES = "resources";
        public static final String ACTIONS = "actions";
        public static final String ACCOUNT_TYPES = "accountTypes";
        public static final String APPROVAL_STATUSES = "approvalStatuses";
        public static final String DOCUMENT_STATUSES = "documentStatuses";
        public static final String PARTNER_ROLES = "partnerRoles";
        public static final String PARTNER_TYPES = "partnerTypes";
        public static final String PRODUCTS = "products";
        public static final String PRODUCT_BARCODES = "productBarcodes";
        public static final String PRODUCT_PRICES = "productPrices";
        public static final String PRODUCT_UNITS = "productUnits";
        public static final String PRODUCT_WAREHOUSES = "productWarehouses";
        public static final String BRANDS = "brands";
        public static final String PRODUCERS = "producers";
        public static final String PRODUCT_CATEGORIES = "productCategories";
        public static final String PRODUCT_CLASSES = "productClasses";
        public static final String PRODUCT_TYPES = "productTypes";
        public static final String PRODUCT_GROUPS_ALL = "productGroups.all";
        public static final String PRODUCT_GROUPS_EXCLUDED = "productGroups.excluded";
        public static final String UNITS_ALL = "units.all";
        public static final String UNITS_BY_DIMENSION_ID = "units.byDimensionId";
        public static final String CURRENCIES = "currencies";
        public static final String COUNTRIES = "countries";
        public static final String WAREHOUSES = "warehouses";
        public static final String PRICE_LISTS_ALL = "priceLists.all";
        public static final String PRICE_LISTS_EXCLUDED = "priceLists.excluded";
        public static final String LOOKUPS = "lookups";
        public static final String PERMISSIONS = "permissions";
        public static final String PERMISSIONS_USER = "permissions.user";
        public static final String PERMISSIONS_ROLE = "permissions.role";
        public static final String AVAILABLE_RESOURCE_ACTIONS_USER = "availableResourceActions.user";
        public static final String AVAILABLE_RESOURCE_ACTIONS_ROLE = "availableResourceActions.role";
        public static final String TRANSACTION_TYPES = "transactionTypes";
    }
}
