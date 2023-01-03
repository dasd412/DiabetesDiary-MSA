package com.dasd412.api.writerservice.adapter.out.web.cookie;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class CookieProvider {

    public ResponseCookie createCookieForRefreshToken(String refreshToken) {
        return null;
    }

    public Cookie createCookie(ResponseCookie cookieForRefreshToken) {
        return null;
    }
}
