package com.orbenox.erp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for API versioning
 * Currently using URI-based versioning: /api/v1/...
 * <p>
 * Note: setUseTrailingSlashMatch was removed in Spring Framework 6.2
 * Trailing slash matching is now disabled by default
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    // Configuration can be added here if needed for future API versioning requirements
}
