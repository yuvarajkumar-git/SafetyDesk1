package com.cts.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cts.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates and validates HS256 JWTs (Story 10).
 * Claims: subject = email, plus userId, role, siteId. Standard iat/exp.
 */
@Slf4j
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${safetydesk.jwt.secret}") String secret,
            @Value("${safetydesk.jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${safetydesk.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        // HS256 requires a key of at least 256 bits (32 bytes). We derive it from the configured secret.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // --- token creation ---

    public String generateAccessToken(User user) {
        return buildToken(user, accessExpirationMs, "ACCESS");
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs, "REFRESH");
    }

    private String buildToken(User user, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().name())     // Java constant name; mapped back on read
                .claim("siteId", user.getSiteId())
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    // --- token reading ---

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Long extractSiteId(String token) {
        return extractClaim(token, claims -> claims.get("siteId", Long.class));
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    public boolean isTokenValid(String token) {
        try {
            // parsing verifies signature AND expiration; throws if invalid/expired
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseClaims(token));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}