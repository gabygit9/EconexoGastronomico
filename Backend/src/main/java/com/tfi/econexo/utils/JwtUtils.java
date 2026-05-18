package com.tfi.econexo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.private.key}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    //Creación de tokens
    public String createToken(Authentication authentication) {

        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        //nombre del usuario principal autenticado. Queda en el context holder.
        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (30*60000))) //30'
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    //Decodificar y validar tokens
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);
        }catch(JWTVerificationException exception){
            throw new JWTVerificationException("Invalid token. Not Authorized");
        }
    }

    //obtener username
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }

    //obtener un claim en particular
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    //obtener todos los claims
    public Map<String,Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
