package com.dasd412.api.writerservice.adapter.out.web.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class CookieProvider {

    @Value("${token.refresh-expired-time}")
    private String refreshExpiredTime;

    private static final String REFRESH_TOKEN = "refresh-token";

    public ResponseCookie createCookieForRefreshToken(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Long.parseLong(refreshExpiredTime)).build();
    }

    public ResponseCookie removeRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN, null)
                .maxAge(0)
                .path("/")
                .build();
    }

    public Cookie createCookie(ResponseCookie responseCookie) {
        Cookie cookie=new Cookie(responseCookie.getName(),responseCookie.getValue());
        cookie.setPath(responseCookie.getPath());
        cookie.setSecure(responseCookie.isSecure());
        cookie.setHttpOnly(responseCookie.isHttpOnly()); //크로스 사이트 스크립팅 방지용 
        cookie.setMaxAge((int) responseCookie.getMaxAge().getSeconds());

        return cookie;
    }
}
