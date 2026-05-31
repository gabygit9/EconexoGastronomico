package com.tfi.econexo.filter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tfi.econexo.security.config.filter.JwtTokenValidator;
import com.tfi.econexo.service.auth.BlacklistedTokenService;
import com.tfi.econexo.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorTest {

    @Mock
    BlacklistedTokenService blacklistedTokenService;

    @Mock
    JwtUtils jwtUtils;

    @InjectMocks
    JwtTokenValidator jwtTokenValidator;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Mock
    DecodedJWT decodedJWT;

    @Test
    void doFilterInternal_tokenIsNotInBlacklist_success() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(blacklistedTokenService.isTokenBlacklisted("token")).thenReturn(false);
        when(jwtUtils.validateToken("token")).thenReturn(decodedJWT);
        when(jwtUtils.extractUsername(decodedJWT)).thenReturn("ana@mail.com");
        when(jwtUtils.getSpecificClaim(decodedJWT, "authorities")).thenReturn(mock(Claim.class));

        jwtTokenValidator.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenIsInTheBlacklist_Unauthorized() throws IOException, ServletException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(blacklistedTokenService.isTokenBlacklisted("token")).thenReturn(true);

        jwtTokenValidator.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalidated");
        verify(filterChain, never()).doFilter(request, response);
    }

}