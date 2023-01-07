package com.dasd412.api.writerservice.adapter.in.web.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.adapter.out.web.dto.JWTTokenDTO;
import com.dasd412.api.writerservice.adapter.out.web.dto.RefreshTokenResponseDTO;
import com.dasd412.api.writerservice.adapter.out.web.exception.InvalidRefreshTokenException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.google.common.net.HttpHeaders;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

@RestController
public class AuthRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RefreshTokenService refreshTokenService;

    private final CookieProvider cookieProvider;

    private final Tracer tracer;

    public AuthRestController(RefreshTokenService refreshTokenService, CookieProvider cookieProvider, Tracer tracer) {
        this.refreshTokenService = refreshTokenService;
        this.cookieProvider = cookieProvider;
        this.tracer = tracer;
    }

    @PostMapping("/refresh")
    @RateLimiter(name = "writerService")
    @CircuitBreaker(name = "writerService", fallbackMethod = "fallBackRefresh")
    public ApiResult<?> refreshToken(HttpServletResponse response, @RequestHeader("X-AUTH-TOKEN") String accessToken,
                                     @CookieValue("refresh-token") String refreshToken) throws TimeoutException {
        logger.info("refreshing token in AuthRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("refreshToken");

        try {
            JWTTokenDTO dto = refreshTokenService.refreshJwtToken(accessToken, refreshToken);

            ResponseCookie cookie = cookieProvider.createCookieForRefreshToken(refreshToken);

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ApiResult.OK(new RefreshTokenResponseDTO(dto));
        } catch (InvalidRefreshTokenException | UserNameExistException e) {
            //refresh 토큰이 유효하지 않으면 쿠키 제거
            ResponseCookie removed = cookieProvider.removeRefreshTokenCookie();

            response.addHeader(HttpHeaders.SET_COOKIE, removed.toString());

            return ApiResult.ERROR(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } finally {
            span.tag("writer.service", "refreshToken");
            span.finish();
        }
    }

    private ApiResult<?> fallBackRefresh(HttpServletResponse response, String accessToken, String refreshToken, Throwable throwable) {
        logger.error("error occurred while refreshing token in AuthRestController:{}", UserContextHolder.getContext().getCorrelationId());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
