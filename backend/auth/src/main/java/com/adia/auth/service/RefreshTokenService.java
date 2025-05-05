package com.adia.auth.service;

import com.adia.auth.entity.RefreshToken;
import com.adia.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Create and persist a refresh token for the given user
     */
    public RefreshToken createRefreshToken(long userId, long expiryMillis) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusNanos(expiryMillis * 1_000_000);

        RefreshToken token = RefreshToken.builder()
                .token(tokenValue)
                .userId(userId)
                .expiryDate(expiryDate)
                .build();

        return refreshTokenRepository.save(token);
    }

    /**
     * Verify token is valid and not expired
     */
    public Optional<RefreshToken> verify(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    /**
     * Delete all tokens for a user (e.g. on logout)
     */
    public void deleteByUserId(long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
