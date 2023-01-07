package com.dasd412.api.writerservice.application.service.security.refresh;

import com.dasd412.api.writerservice.adapter.out.web.dto.JWTTokenDTO;

import java.util.concurrent.TimeoutException;

public interface RefreshTokenService {

    void updateRefreshToken(Long userId, String uuid);

    JWTTokenDTO refreshJwtToken(String accessToken, String refreshToken)throws TimeoutException;
}
