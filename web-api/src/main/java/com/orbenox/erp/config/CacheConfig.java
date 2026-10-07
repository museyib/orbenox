package com.orbenox.erp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static com.orbenox.erp.config.CacheConfig.CacheNames.*;

/**
 * Cache configuration using Caffeine for better performance
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                USER_DETAILS,
                HAS_PERMISSION,
                USERS,
                ROLES,
                RESOURCES,
                ACTIONS,
                PRODUCTS,
                PRODUCT_BARCODES,
                BRANDS,
                PRODUCERS,
                PRODUCT_CATEGORIES,
                PRODUCT_CLASSES,
                PRODUCT_TYPES,
                PRODUCT_GROUPS_ALL,
                PRODUCT_GROUPS_EXCLUDED,
                UNITS_ALL,
                UNITS_BY_DIMENSION_ID,
                UNIT_DIMENSIONS,
                CURRENCIES,
                COUNTRIES,
                WAREHOUSES,
                ACCOUNTS,
                BUSINESS_PARTNERS,
                BUSINESS_PARTNER_ROLES,
                PRICE_LISTS_ALL,
                PRICE_LISTS_EXCLUDED,
                LOOKUPS,
                PERMISSIONS,
                TRANSACTION_TYPES,
                ACCOUNT_TYPES,
                AMOUNT_SOURCES,
                APPROVAL_STATUSES,
                DOCUMENT_STATUSES,
                PARTNER_SIDES,
                PARTNER_ROLES,
                PARTNER_TYPES,
                POSTING_RULES,
                PERMISSIONS_USER,
                PERMISSIONS_ROLE,
                AVAILABLE_RESOURCE_ACTIONS_USER,
                AVAILABLE_RESOURCE_ACTIONS_ROLE,
                PRODUCT_PRICES,
                PRODUCT_UNITS,
                PRODUCT_WAREHOUSES,
                RESET_PERIODS,
                STOCK_AFFECT_DIRECTIONS
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
        public static final String AMOUNT_SOURCES = "amountSources";
        public static final String APPROVAL_STATUSES = "approvalStatuses";
        public static final String DOCUMENT_STATUSES = "documentStatuses";
        public static final String PARTNER_SIDES = "partnerSides";
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
        public static final String UNIT_DIMENSIONS = "unitDimensions";
        public static final String CURRENCIES = "currencies";
        public static final String COUNTRIES = "countries";
        public static final String WAREHOUSES = "warehouses";
        public static final String ACCOUNTS = "accounts";
        public static final String BUSINESS_PARTNERS = "businessPartners";
        public static final String BUSINESS_PARTNER_ROLES = "businessPartnerRoles";
        public static final String POSTING_RULES = "postingRules";
        public static final String PRICE_LISTS_ALL = "priceLists.all";
        public static final String PRICE_LISTS_EXCLUDED = "priceLists.excluded";
        public static final String LOOKUPS = "lookups";
        public static final String PERMISSIONS = "permissions";
        public static final String PERMISSIONS_USER = "permissions.user";
        public static final String PERMISSIONS_ROLE = "permissions.role";
        public static final String AVAILABLE_RESOURCE_ACTIONS_USER = "availableResourceActions.user";
        public static final String AVAILABLE_RESOURCE_ACTIONS_ROLE = "availableResourceActions.role";
        public static final String TRANSACTION_TYPES = "transactionTypes";
        public static final String RESET_PERIODS = "resetPeriods";
        public static final String STOCK_AFFECT_DIRECTIONS = "stockAffectDirections";
    }
}
