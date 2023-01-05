package com.dasd412.api.writerservice.application.service.cache.refresh;

public interface RefreshTokenService {

    void updateRefreshToken(Long userId, String uuid);
}
