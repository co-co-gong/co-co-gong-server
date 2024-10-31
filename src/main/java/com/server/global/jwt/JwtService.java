package com.server.global.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String username) {
        return createToken(username, accessTokenExpiration);
    }

    public String createRefreshToken(String username) {
        return createToken(username, refreshTokenExpiration);
    }

    private String createToken(String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                .setSubject(username)
                .claim(USERNAME_CLAIM, username) // 명시적으로 USERNAME_CLAIM 추가
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        log.info("Created token for user: {}, token: {}", username, token);
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            log.info("Token is valid");
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Optional<String> extractUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.get(USERNAME_CLAIM, String.class);
            if (username == null) {
                username = claims.getSubject(); // USERNAME_CLAIM이 없으면 subject를 사용
            }
            log.info("Extracted username from token: {}", username);
            return Optional.ofNullable(username);
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader);

        return Optional.ofNullable(authHeader)
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> {
                    String token = accessToken.substring(BEARER.length()).trim();
                    log.info("Extracted access token: {}", token);
                    return token;
                });
    }

    public Optional<String> extractUsernameFromToken(HttpServletRequest request) {
        Optional<String> accessToken = extractAccessToken(request);
        if (accessToken.isEmpty()) {
            log.warn("Access token is empty");
            return Optional.empty();
        }

        if (!validateToken(accessToken.get())) {
            log.warn("Token is invalid");
            return Optional.empty();
        }

        return extractUsername(accessToken.get());
    }
}
