package com.dasd412.api.writerservice.application.service.security;

import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.in.security.PrincipalDetails;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class JWTTokenWriterIntoResponseBody {

    private final JWTTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieProvider cookieProvider;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JWTTokenWriterIntoResponseBody(JWTTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, CookieProvider cookieProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.cookieProvider = cookieProvider;
    }

    //facade 패턴 클래스
    public Map<String, Object>makeTokenForResponseBody(PrincipalDetails principalDetails, HttpServletRequest request, HttpServletResponse response){
        logger.info("writing jwt token into response body ...");

        //access token과 만료 시간은 response body에 담는다.
        String accessToken = jwtTokenProvider.issueNewJwtAccessToken(principalDetails.getUsername(), principalDetails.getWriter().getId(), request.getRequestURI(), principalDetails.getAuthorities());

        LocalDateTime expired = LocalDateTime.ofInstant(jwtTokenProvider.retrieveExpiredTime(accessToken).toInstant(), ZoneId.systemDefault());

        //access token이 탈취 당할 경우 보안에 취약해진다. 이를 대비하기 위한 것이 refresh token.
        String refreshToken = jwtTokenProvider.issueJwtRefreshToken();

        //refresh token은 로그인 할 때 발급이 빨라야 하고, 일정 기간 이후 만료되어야 하므로 redis에서 관리한다.
        refreshTokenService.updateRefreshToken(principalDetails.getWriter().getId(), jwtTokenProvider.retrieveRefreshTokenId(refreshToken));

        //refresh token은 쿠키에 담는다.
        ResponseCookie cookieForRefreshToken = cookieProvider.createCookieForRefreshToken(refreshToken);

        Cookie cookie = cookieProvider.createCookie(cookieForRefreshToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addCookie(cookie);

        return Map.of(
                "accessToken", accessToken,
                "expired", expired.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
