package com.dasd412.api.writerservice.application.service.security.validation.impl;

import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.in.security.exception.InvalidAccessTokenException;
import com.dasd412.api.writerservice.application.service.security.validation.AccessTokenService;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final JWTTokenProvider jwtTokenProvider;

    public AccessTokenServiceImpl(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void validateAccessToken(String authorization) {
        String token=authorization.replace("Bearer", "");

        if(!jwtTokenProvider.validateJwtToken(token)){
            throw new InvalidAccessTokenException("this token is invalid");
        }
    }
}
