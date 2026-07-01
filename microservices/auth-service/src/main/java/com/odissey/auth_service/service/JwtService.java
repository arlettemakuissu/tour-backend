package com.odissey.auth_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.odissey.auth_service.config.AuthProperties;
import com.odissey.auth_service.entity.RefreshToken;
import com.odissey.auth_service.entity.User;
import com.odissey.auth_service.exception.AuthException;
import com.odissey.auth_service.exception.ErrMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    private final AuthProperties authProperties;
    private final JWSSigner signer;

    public JwtService(AuthProperties authProperties) throws KeyLengthException {
        this.authProperties = authProperties;
        this.signer = new MACSigner(authProperties.getHmacSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String jwtToken(User user) {

        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId())) // id dello user
                .issuer(authProperties.getIssuer()) // emittente del token
                .audience(authProperties.getAudience())  // audience
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(authProperties.getAccessTokenTtlSeconds())))
                .claim(authProperties.getRolesClaim(), user.getRoles())
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(),
                claims
        );
        try{
            jwt.sign(signer);
            return jwt.serialize();
        } catch(JOSEException ex){
            log.error(">>> Error Unsignable JWT: "+ex.getMessage());
            throw new AuthException(ErrMsg.UNSIGNABLE_JWT);
        }
    }

    public String jwtRefreshToken(User user, RefreshToken refreshToken) {

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId())) // id dello user
                .issuer(authProperties.getIssuer()) // emittente del token
                .audience(authProperties.getAudience())  // audience
                .issueTime(Date.from(refreshToken.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()))
                .expirationTime(Date.from(refreshToken.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant()))
                .claim(authProperties.getRolesClaim(), user.getRoles())
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(),
                claims
        );
        try{
            jwt.sign(signer);
            return jwt.serialize();
        } catch(JOSEException ex){
            log.error(">>> Error Unsignable JWT: "+ex.getMessage());
            throw new AuthException(ErrMsg.UNSIGNABLE_JWT);
        }
    }
}
