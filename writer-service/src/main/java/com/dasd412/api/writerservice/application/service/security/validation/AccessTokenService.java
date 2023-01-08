package com.dasd412.api.writerservice.application.service.security.validation;

public interface AccessTokenService {

    void validateAccessToken(String authorization);
}
