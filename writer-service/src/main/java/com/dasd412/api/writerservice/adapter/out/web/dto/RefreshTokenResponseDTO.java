package com.dasd412.api.writerservice.adapter.out.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDTO {

    private String accessToken;

    private String expiredTime;

    public RefreshTokenResponseDTO(JWTTokenDTO jwtTokenDTO){
        this.accessToken= jwtTokenDTO.getAccessToken();
        this.expiredTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(jwtTokenDTO.getAccessTokenExpiredDate());
    }
}
