package com.dasd412.api.writerservice.application.service.security.refresh.impl;

import com.dasd412.api.writerservice.adapter.in.security.RefreshToken;
import com.dasd412.api.writerservice.adapter.out.cache.RefreshTokenRepository;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final WriterRepository writerRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(WriterRepository writerRepository, RefreshTokenRepository refreshTokenRepository) {
        this.writerRepository = writerRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public void updateRefreshToken(Long userId, String uuid) {
        Writer writer=writerRepository.findById(userId).orElseThrow(()-> new NoResultException("writer not exist while updating refresh token"));

        refreshTokenRepository.save(RefreshToken.of(writer.getId().toString(), uuid));
    }
}
