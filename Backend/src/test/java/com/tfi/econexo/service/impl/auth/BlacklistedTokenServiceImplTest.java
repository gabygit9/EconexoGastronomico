package com.tfi.econexo.service.impl.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tfi.econexo.model.auth.BlacklistedToken;
import com.tfi.econexo.repository.auth.BlacklistedTokenRepository;
import com.tfi.econexo.service.auth.BlacklistedTokenServiceImpl;
import com.tfi.econexo.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlacklistedTokenServiceImplTest {

    @Mock
    private BlacklistedTokenRepository repository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private BlacklistedTokenServiceImpl blacklistedTokenService;

    @Mock
    DecodedJWT decodedJWT;

    @Test
    void blacklistToken_success(){
        when(repository.existsByToken(any())).thenReturn(false);
        when(decodedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 86400000L));
        when(jwtUtils.validateToken(any())).thenReturn(decodedJWT);

        blacklistedTokenService.blacklistToken("token");

        verify(repository).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_tokenAlreadyBlacklisted(){
        when(repository.existsByToken(any())).thenReturn(true);

        blacklistedTokenService.blacklistToken("token");

        verify(repository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_tokenInvalid_throwsJWTVerificationException(){
        when(jwtUtils.validateToken(any())).thenThrow(new JWTVerificationException("Invalid token. Not Authorized"));

        assertThrows(JWTVerificationException.class, () -> blacklistedTokenService.blacklistToken("token"));
        verify(repository, never()).save(any(BlacklistedToken.class));
    }
}