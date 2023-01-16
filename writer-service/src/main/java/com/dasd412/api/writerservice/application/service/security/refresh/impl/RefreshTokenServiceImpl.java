package com.dasd412.api.writerservice.application.service.security.refresh.impl;

import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.in.security.exception.InvalidAccessTokenException;
import com.dasd412.api.writerservice.adapter.out.web.dto.JWTTokenDTO;
import com.dasd412.api.writerservice.adapter.out.web.exception.InvalidRefreshTokenException;
import com.dasd412.api.writerservice.application.service.security.PrincipalDetailsService;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshToken;
import com.dasd412.api.writerservice.adapter.out.cache.RefreshTokenRepository;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final WriterRepository writerRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JWTTokenProvider jwtTokenProvider;

    private final PrincipalDetailsService principalDetailsService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RefreshTokenServiceImpl(WriterRepository writerRepository, RefreshTokenRepository refreshTokenRepository, JWTTokenProvider jwtTokenProvider, PrincipalDetailsService principalDetailsService) {
        this.writerRepository = writerRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.principalDetailsService = principalDetailsService;
    }

    @Override
    @Transactional
    public void updateRefreshToken(Long userId, String uuid) {
        logger.info("updating refresh token in refresh token service : {}", UserContextHolder.getContext().getCorrelationId());
        Writer writer = writerRepository.findById(userId).orElseThrow(() -> new NoResultException("writer not exist while updating refresh token"));

        refreshTokenRepository.save(RefreshToken.of(writer.getId().toString(), uuid));
    }

    @Override
    @Transactional
    public JWTTokenDTO refreshJwtToken(String accessToken, String refreshToken) throws TimeoutException {
        logger.info("updating refresh token in refresh token service : {}", UserContextHolder.getContext().getCorrelationId());

        String userName = jwtTokenProvider.retrieveUserName(accessToken);

        String userId = jwtTokenProvider.retrieveWriterId(accessToken);

        RefreshToken found = refreshTokenRepository.findById(userId).orElseThrow(() -> new NoResultException("refresh token not exist"));

        logger.info("validating refresh token...");

        String tokenIdOfFound = found.getRefreshTokenId();

        if (!jwtTokenProvider.validateJwtToken(refreshToken)) {
            refreshTokenRepository.delete(found);
            throw new InvalidRefreshTokenException("Invalid refresh token" + refreshToken);
        }

        if (!jwtTokenProvider.equalRefreshTokenId(tokenIdOfFound, refreshToken)) {
            throw new InvalidRefreshTokenException("Unmatched refresh token in redis" + refreshToken);
        }

        Long writerId = Long.parseLong(jwtTokenProvider.retrieveWriterId(accessToken));

        writerRepository.findById(writerId).orElseThrow(() -> new NoResultException("writer not exist"));

        logger.info("creating access token...");

        Authentication authentication = getAuthentication(userName);

        String newAccessToken = jwtTokenProvider.issueNewJwtAccessToken(userName, writerId, "/refresh", authentication.getAuthorities());

        Date expired = jwtTokenProvider.retrieveExpiredTime(newAccessToken);

        return JWTTokenDTO.builder()
                .accessToken(newAccessToken)
                .accessTokenExpiredDate(expired)
                .refreshToken(refreshToken)
                .build();
    }

    private Authentication getAuthentication(String username) {
        UserDetails principalDetails = principalDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(principalDetails, principalDetails.getPassword());
    }


    @Override
    @Transactional
    public void logoutToken(String accessToken)throws TimeoutException {
        logger.info("logout and deleting refresh token in RefreshTokenService :{}", UserContextHolder.getContext().getCorrelationId());

        if (!jwtTokenProvider.validateJwtToken(accessToken)) {
            throw new InvalidAccessTokenException("this is invalid access token");
        }

        String userId = jwtTokenProvider.retrieveWriterId(accessToken);

        RefreshToken refreshToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new InvalidRefreshTokenException("refresh token not exist"));

        refreshTokenRepository.delete(refreshToken);
    }
}
