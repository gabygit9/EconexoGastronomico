package com.tfi.econexo.security.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.tfi.econexo.service.auth.BlacklistedTokenService;
import com.tfi.econexo.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@AllArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final BlacklistedTokenService blacklistedTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken != null && jwtToken.startsWith("Bearer ")){
            //borrar bearer + espacio
            jwtToken = jwtToken.substring(7);

            if(blacklistedTokenService.isTokenBlacklisted(jwtToken)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalidated");
                return;
            }

            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            String username = jwtUtils.extractUsername(decodedJWT);
            String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            Collection <? extends GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesList);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.contains("/api/v1/auth/login") ||
                path.contains("/api/v1/auth/register") ||
                path.contains("/api/v1/neighborhoods/public") ||
                path.contains("/api/v1/donors/public/donor-types") ||
                path.contains("/api/v1/organizations/public/ngo-types") ||
                path.contains("/api/v1/uploads") ||
                path.contains("/api/v1/auth/password-reset") ||
                path.contains("/api/v1/public");
    }
}
