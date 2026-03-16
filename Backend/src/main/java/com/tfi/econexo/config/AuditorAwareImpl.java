package com.tfi.econexo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

@Configuration
public class AuditorAwareImpl {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> Optional.ofNullable(getCurrentUserId());
    }

    private static Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return 0L;
            }
            Object principal = authentication.getPrincipal();
            if (principal == null) {
                return 0L;
            }

            // If principal exposes an id property, try to use it (common in custom UserDetails)
            try {
                Method getId = principal.getClass().getMethod("getId");
                Object idVal = getId.invoke(principal);
                if (idVal instanceof Number) {
                    return ((Number) idVal).longValue();
                }
                if (idVal instanceof String) {
                    try {
                        return Long.parseLong((String) idVal);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } catch (NoSuchMethodException ignored) {
                // ignore and try other strategies
            }

            // If principal is a UserDetails, try username -> parse as long
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                try {
                    return Long.valueOf(username);
                } catch (NumberFormatException ignored) {
                    // cannot parse username to id
                }
            }

            // If principal is a Map (like JWT token claims), try to read id key
            if (principal instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) principal;
                Object idObj = map.get("id");
                if (idObj == null) idObj = map.get("user_id");
                if (idObj instanceof Number) return ((Number) idObj).longValue();
                if (idObj instanceof String) {
                    try {
                        return Long.parseLong((String) idObj);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // As a last resort, try to parse authentication.getName()
            String name = authentication.getName();
            if (name != null) {
                try {
                    return Long.parseLong(name);
                } catch (NumberFormatException ignored) {
                }
            }

            // Fallback
            return 0L;
        } catch (NoClassDefFoundError | Exception ex) {
            // If Spring Security is not on the classpath or any error occurs, fallback to 0L
            return 0L;
        }
    }
}
