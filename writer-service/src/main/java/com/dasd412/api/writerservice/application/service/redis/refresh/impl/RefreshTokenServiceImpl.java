package com.dasd412.api.writerservice.application.service.redis.refresh.impl;

import com.dasd412.api.writerservice.application.service.redis.refresh.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Override
    @Transactional
    public void updateRefreshToken(Long userId, String uuid) {

    }
}
