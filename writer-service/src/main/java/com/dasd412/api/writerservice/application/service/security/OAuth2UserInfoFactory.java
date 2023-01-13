package com.dasd412.api.writerservice.application.service.security;

import com.dasd412.api.writerservice.application.service.security.provider.GitHubUserInfo;
import com.dasd412.api.writerservice.application.service.security.provider.GoogleUserInfo;
import com.dasd412.api.writerservice.application.service.security.provider.OAuth2UserInfo;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OAuth2UserInfoFactory {

    public Optional<OAuth2UserInfo> selectOAuth2UserInfo(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        switch (registrationId) {
            case "google":
                return Optional.of(new GoogleUserInfo(oAuth2User.getAttributes()));
            case "github":
                return Optional.of(new GitHubUserInfo(oAuth2User.getAttributes()));
            default:
                return Optional.empty();
        }
    }
}
