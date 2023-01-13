package com.dasd412.api.writerservice.adapter.in.security.filter;


import com.dasd412.api.writerservice.adapter.in.security.PrincipalDetails;
import com.dasd412.api.writerservice.adapter.in.security.exception.LoginBadRequestException;
import com.dasd412.api.writerservice.adapter.in.security.dto.LoginRequestDTO;

import com.dasd412.api.writerservice.application.service.security.JWTTokenWriterIntoResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenWriterIntoResponseBody facade;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JWTTokenWriterIntoResponseBody facade) {
        this.authenticationManager = authenticationManager;
        this.facade = facade;
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

            //인증 토큰 만들기. loadUserByUsername()에서 판별되는 토큰이다.
            //loadUserByUsername()의 인자와 UsernamePasswordAuthenticationToken의 첫 번째 인자가 동일해야 로그인이 된다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            //loadUserByUsername()을 호출하고, session에 인증 객체 저장. jwt 토큰이라 세션이 필요 없지만, 인가를 위해 저장됨.
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException ioException) {
            throw new LoginBadRequestException(ioException.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info("success login in JwtFilter");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        Map<String, Object> responseBody = facade.makeTokenForResponseBody(principalDetails, request, response);

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }
}
