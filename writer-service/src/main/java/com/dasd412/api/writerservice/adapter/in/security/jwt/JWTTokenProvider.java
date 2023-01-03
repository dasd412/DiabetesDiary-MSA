package com.dasd412.api.writerservice.adapter.in.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class JWTTokenProvider {

    @Value("${token.access-expired-time}")
    private long ACCESS_EXPIRED_TIME;

    @Value("${token.refresh-expired-time}")
    private long REFRESH_EXPIRED_TIME;

    @Value("${token.secret}")
    private String SECRET;

    public String issueNewJwtAccessToken(String username, String requestURI, Collection<? extends GrantedAuthority> authorities) {
        return null;
    }

    public LocalDateTime issueExpiredTime(String accessToken) {
        return null;
    }

    public String issueJwtRefreshToken() {
        return null;
    }

    public String issueRefreshTokenId(String refreshToken) {
        return null;
    }
}
