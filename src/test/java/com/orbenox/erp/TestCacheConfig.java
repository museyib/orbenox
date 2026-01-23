package com.orbenox.erp;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestCacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new SimpleCacheManager();
    }
}
