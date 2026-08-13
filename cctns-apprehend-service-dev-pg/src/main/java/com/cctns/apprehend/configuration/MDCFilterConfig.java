package com.cctns.apprehend.configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilterConfig implements Filter {

    private static final String CORRELATION_ID = "correlationId";
    private static final String HEADER_NAME = "X-Request-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String correlationId = getCorrelationIdFromHeadersOrGenerate((HttpServletRequest) request);
            MDC.put(CORRELATION_ID, correlationId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    private String getCorrelationIdFromHeadersOrGenerate(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_NAME);
        return (headerValue == null || headerValue.isBlank())
                ? UUID.randomUUID().toString()
                : headerValue;
    }
}

