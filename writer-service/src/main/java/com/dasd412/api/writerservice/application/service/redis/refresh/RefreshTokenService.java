package com.dasd412.api.writerservice.application.service.redis.refresh;

public interface RefreshTokenService {

    void updateRefreshToken(Long userId, String uuid);
}
