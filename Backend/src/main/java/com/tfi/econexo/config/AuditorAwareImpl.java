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
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Si no hay nadie logueado
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return Optional.of(0L);
            }

            try {

                // TODO: Descomentar esto cuando se cree la clase UserDetailsImpl
                /*
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                return Optional.of(userDetails.getId());
                */

                return Optional.of(1L); // Temporal hasta que hagamos el UserDetailsImpl

            } catch (Exception e) {
                return Optional.of(0L);
            }
        };
    }
}
