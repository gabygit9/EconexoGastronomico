package com.tfi.econexo.service.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.tfi.econexo.model.auth.BlacklistedToken;
import com.tfi.econexo.repository.auth.BlacklistedTokenRepository;
import com.tfi.econexo.service.auth.BlacklistedTokenService;
import com.tfi.econexo.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenServiceImpl implements BlacklistedTokenService {

    private final BlacklistedTokenRepository repository;
    private final JwtUtils jwtUtils;


    @Override
    public void blacklistToken(String token) {
        if (repository.existsByToken(token)) {return;}

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        Date expirationDate = decodedJWT.getExpiresAt();

        repository.save(new BlacklistedToken(token, expirationDate));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}
