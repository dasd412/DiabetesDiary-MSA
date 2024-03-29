package com.dasd412.api.writerservice.adapter.in.web.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.writerservice.adapter.in.security.exception.InvalidAccessTokenException;
import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.adapter.out.web.dto.JWTTokenDTO;
import com.dasd412.api.writerservice.adapter.out.web.dto.RefreshTokenResponseDTO;
import com.dasd412.api.writerservice.adapter.out.web.exception.InvalidRefreshTokenException;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import com.dasd412.api.writerservice.application.service.security.validation.AccessTokenService;
import com.dasd412.api.writerservice.application.service.writer.DeleteWriterService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.google.common.net.HttpHeaders;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

@RestController
public class AuthRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RefreshTokenService refreshTokenService;

    private final AccessTokenService accessTokenService;

    private final CookieProvider cookieProvider;

    private final DeleteWriterService deleteWriterService;

    private final Tracer tracer;

    public AuthRestController(RefreshTokenService refreshTokenService, AccessTokenService accessTokenService, CookieProvider cookieProvider, DeleteWriterService deleteWriterService, Tracer tracer) {
        this.refreshTokenService = refreshTokenService;
        this.accessTokenService = accessTokenService;
        this.cookieProvider = cookieProvider;
        this.deleteWriterService = deleteWriterService;
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
        } catch (InvalidRefreshTokenException e) {
            e.printStackTrace();

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

    @PostMapping("/auth/logout")
    @RateLimiter(name = "writerService")
    @CircuitBreaker(name = "writerService", fallbackMethod = "fallBackLogout")
    public ApiResult<?> logout(HttpServletResponse response, @RequestHeader("X-AUTH-TOKEN") String accessToken) throws TimeoutException {
        logger.info("logout in AuthRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("logout");

        try {
            refreshTokenService.logoutToken(accessToken);

            ResponseCookie refreshTokenCookie = cookieProvider.removeRefreshTokenCookie();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ApiResult.OK(HttpStatus.OK);
        } catch (InvalidAccessTokenException | InvalidRefreshTokenException e) {
            return ApiResult.ERROR("logout fail", HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("writer.service", "logout");
            span.finish();
        }
    }

    private ApiResult<?> fallBackLogout(HttpServletResponse response, String accessToken, Throwable throwable) {
        logger.error("error occurred while logout in AuthRestController:{}", UserContextHolder.getContext().getCorrelationId());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/validation/access-token")
    public ApiResult<?> validateAccessToken(@RequestHeader(name = "Authorization") String authorization) {
        try {
            accessTokenService.validateAccessToken(authorization);
            return ApiResult.OK(HttpStatus.OK);
        } catch (InvalidAccessTokenException e) {
            return ApiResult.ERROR(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/withdrawal")
    @RateLimiter(name = "writerService")
    @CircuitBreaker(name = "writerService")
    public ApiResult<?> withdrawWriter(@RequestHeader("X-AUTH-TOKEN") String accessToken) {
        logger.info("withdraw in AuthRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("withdrawal");

        try {
            Long writerId = deleteWriterService.removeWriter(accessToken);
            deleteWriterService.sendMessageToOtherService(writerId);
            return ApiResult.OK(HttpStatus.OK);
        } catch (InvalidAccessTokenException e) {
            return ApiResult.ERROR(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("writer.service", "withdrawal");
            span.finish();
        }
    }
}
