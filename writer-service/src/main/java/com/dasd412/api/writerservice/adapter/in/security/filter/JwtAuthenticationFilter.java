package com.dasd412.api.writerservice.adapter.in.security.filter;


import com.dasd412.api.writerservice.adapter.in.security.auth.PrincipalDetails;
import com.dasd412.api.writerservice.adapter.in.security.exception.LoginBadRequestException;
import com.dasd412.api.writerservice.adapter.in.security.dto.LoginRequestDTO;
import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieProvider cookieProvider;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, CookieProvider cookieProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.cookieProvider = cookieProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("attempting login in JwtFilter");

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            LoginRequestDTO dto = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);

            //?????? ?????? ?????????. loadUserByUsername()?????? ???????????? ????????????.
            //loadUserByUsername()??? ????????? UsernamePasswordAuthenticationToken??? ??? ?????? ????????? ???????????? ???????????? ??????.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            //loadUserByUsername()??? ????????????, session??? ?????? ?????? ??????. jwt ???????????? ????????? ?????? ?????????, ????????? ?????? ?????????.
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException ioException) {
            throw new LoginBadRequestException(ioException.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info("success login in JwtFilter");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        //access token??? ?????? ????????? response body??? ?????????.
        String accessToken = jwtTokenProvider.issueNewJwtAccessToken(principalDetails.getUsername(), principalDetails.getWriter().getId(), request.getRequestURI(), principalDetails.getAuthorities());

        LocalDateTime expired = LocalDateTime.ofInstant(jwtTokenProvider.retrieveExpiredTime(accessToken).toInstant(), ZoneId.systemDefault());

        //access token??? ?????? ?????? ?????? ????????? ???????????????. ?????? ???????????? ?????? ?????? refresh token.
        String refreshToken = jwtTokenProvider.issueJwtRefreshToken();

        //refresh token??? ????????? ??? ??? ????????? ????????? ??????, ?????? ?????? ?????? ??????????????? ????????? redis?????? ????????????.
        refreshTokenService.updateRefreshToken(principalDetails.getWriter().getId(), jwtTokenProvider.retrieveRefreshTokenId(refreshToken));

        //refresh token??? ????????? ?????????.
        ResponseCookie cookieForRefreshToken = cookieProvider.createCookieForRefreshToken(refreshToken);

        Cookie cookie = cookieProvider.createCookie(cookieForRefreshToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addCookie(cookie);

        Map<String, Object> responseBody = Map.of(
                "accessToken", accessToken,
                "expired", expired.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }
}
