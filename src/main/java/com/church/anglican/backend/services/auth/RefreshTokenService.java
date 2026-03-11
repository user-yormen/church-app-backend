package com.church.anglican.backend.services.auth;

import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.RefreshToken;
import com.church.anglican.backend.repositories.identity.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenTtlDays;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.security.jwt.refresh-ttl-days:7}") long refreshTokenTtlDays
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public String createRefreshToken(AppUser user) {
        String rawToken = generateTokenValue();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenTtlDays));
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    public RefreshToken verifyRefreshToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getRevokedAt() != null) {
            throw new RuntimeException("Refresh token revoked");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public void revoke(RefreshToken token) {
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    private String generateTokenValue() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to hash refresh token", ex);
        }
    }
}
