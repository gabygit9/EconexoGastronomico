package com.tfi.econexo.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @BeforeAll
    static void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "privateKey", "Private key");
        ReflectionTestUtils.setField(jwtUtils, "userGenerator", "Gabriela");
        ReflectionTestUtils.setField(jwtUtils, "expirationMinutes", 30L);
    }

    @Test
    void createToken_success() {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getPrincipal()).thenReturn("Gabriela");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        String token = jwtUtils.createToken(authentication);
        assertNotNull(token);

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        assertEquals("Gabriela", jwtUtils.extractUsername(decodedJWT));
    }

    @Test
    void validateToken_incorrectToken() {
        String token = "Invalid Token";
        assertThrows(JWTVerificationException.class, () -> jwtUtils.validateToken(token));
    }
}