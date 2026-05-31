package com.tfi.econexo.service.auth;

public interface BlacklistedTokenService {

    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);
}
