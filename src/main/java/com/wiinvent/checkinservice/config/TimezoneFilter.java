package com.wiinvent.checkinservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;

/**
 * Filter đặt ZoneId cho request dựa trên header "X-Timezone" (IANA name).
 * Nếu header thiếu hoặc invalid => fallback to default from config.
 */
@Component
public class TimezoneFilter extends HttpFilter {

    @Value("${app.default-timezone:UTC}")
    private String defaultTimezone;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            String tz = req.getHeader("X-Timezone");
            ZoneId zone;
            if (tz != null && !tz.isBlank()) {
                try {
                    zone = ZoneId.of(tz.trim());
                } catch (Exception e) {
                    zone = ZoneId.of(defaultTimezone);
                }
            } else {
                zone = ZoneId.of(defaultTimezone);
            }
            RequestContext.setZoneId(zone);
            chain.doFilter(req, res);
        } finally {
            RequestContext.clear();
        }
    }
}