package com.orbenox.erp.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application metrics using Micrometer
 */
@Configuration
public class MetricsConfig {

    @Bean
    public Counter productCreatedCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.product.created")
                .description("Number of products created")
                .tag("type", "business")
                .register(registry);
    }

    @Bean
    public Counter productUpdatedCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.product.updated")
                .description("Number of products updated")
                .tag("type", "business")
                .register(registry);
    }

    @Bean
    public Counter productDeletedCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.product.deleted")
                .description("Number of products deleted")
                .tag("type", "business")
                .register(registry);
    }

    @Bean
    public Counter userLoginCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.user.login")
                .description("Number of user logins")
                .tag("type", "security")
                .register(registry);
    }

    @Bean
    public Counter authenticationFailedCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.authentication.failed")
                .description("Number of failed authentication attempts")
                .tag("type", "security")
                .register(registry);
    }

    @Bean
    public Timer documentProcessingTimer(MeterRegistry registry) {
        return Timer.builder("orbenox.document.processing.time")
                .description("Time taken to process documents")
                .tag("type", "performance")
                .register(registry);
    }

    @Bean
    public Counter documentPostedCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.document.posted")
                .description("Number of documents posted")
                .tag("type", "business")
                .register(registry);
    }

    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.cache.hit")
                .description("Number of cache hits")
                .tag("type", "performance")
                .register(registry);
    }

    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("orbenox.cache.miss")
                .description("Number of cache misses")
                .tag("type", "performance")
                .register(registry);
    }
}
