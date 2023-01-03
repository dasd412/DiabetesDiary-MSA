package com.dasd412.api.writerservice.adapter.in.security.jwt;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@RedisHash("refresh_token")
public class RefreshToken {

    @Id
    private String userId;

    private String refreshTokenId;

    private RefreshToken() {
    }

    public static RefreshToken of(String userId, String refreshTokenId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.userId = userId;
        refreshToken.refreshTokenId = refreshTokenId;
        return refreshToken;
    }
}
