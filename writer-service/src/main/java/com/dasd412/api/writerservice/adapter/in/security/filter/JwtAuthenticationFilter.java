package com.dasd412.api.writerservice.adapter.in.security.filter;


import com.dasd412.api.writerservice.adapter.in.security.auth.PrincipalDetails;
import com.dasd412.api.writerservice.adapter.in.security.exception.LoginBadRequestException;
import com.dasd412.api.writerservice.adapter.in.security.dto.LoginRequestDTO;
import com.dasd412.api.writerservice.adapter.in.security.jwt.JWTTokenProvider;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("attempting login in JwtFilter:{}", UserContextHolder.getContext().getCorrelationId());

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            LoginRequestDTO dto = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);

            //인증 토큰 만들기. loadUserByUsername()에서 판별되는 토큰이다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            //loadUserByUsername()을 호출하고, session에 인증 객체 저장. jwt 토큰이라 세션이 필요 없지만, 인가를 위해 저장됨.
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException ioException) {
            throw new LoginBadRequestException(ioException.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info("success login in JwtFilter:{}", UserContextHolder.getContext().getCorrelationId());

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        //todo 토큰 발급에 대해 마저 작성해야 함

    }
}
