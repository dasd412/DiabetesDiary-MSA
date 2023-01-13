package com.dasd412.api.writerservice.application.service.security.provider;

public interface OAuth2UserInfo {

    String getProvider();
    /**
     * @return OAuth provider 가 제공한 "유일한" 식별자 값
     */
    String getProviderId();//

    String getEmail();

    String getName();
}
