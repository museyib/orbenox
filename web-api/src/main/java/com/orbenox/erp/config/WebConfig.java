package com.orbenox.erp.config;

import com.orbenox.erp.localization.LocalizationService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final LocalizationService i18n;

    @Bean
    public FilterRegistrationBean<RequestSizeLimitFilter> requestSizeLimitFilter() {
        FilterRegistrationBean<RequestSizeLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestSizeLimitFilter(i18n));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    /**
     * Filter to enforce request size limits and add correlation IDs
     */
    @RequiredArgsConstructor
    public static class RequestSizeLimitFilter implements Filter {
        private static final long MAX_REQUEST_SIZE = 10 * 1024 * 1024; // 10MB
        private final LocalizationService i18n;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Add correlation ID for request tracking
            String correlationId = UUID.randomUUID().toString();
            org.slf4j.MDC.put("correlationId", correlationId);

            try {
                // Check content length
                long contentLength = httpRequest.getContentLengthLong();
                if (contentLength > MAX_REQUEST_SIZE) {
                    throw new IllegalStateException(i18n.msg("error.request.size.exceeded", 10));
                }

                chain.doFilter(request, response);
            } finally {
                org.slf4j.MDC.clear();
            }
        }
    }
}
