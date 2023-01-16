package com.dasd412.api.writerservice.adapter.out.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class JWTTokenDTO {

    private final String accessToken;

    private final Date accessTokenExpiredDate;

    private final String refreshToken;

    @Builder
    public JWTTokenDTO(String accessToken, Date accessTokenExpiredDate, String refreshToken) {
        this.accessToken = accessToken;
        this.accessTokenExpiredDate = accessTokenExpiredDate;
        this.refreshToken = refreshToken;
    }
}
