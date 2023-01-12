package com.dasd412.api.writerservice.application.service.security;

import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.application.service.authority.AuthorityService;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writerauthority.WriterAuthorityService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JWTTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieProvider cookieProvider;

    private final SaveWriterService saveWriterService;

    private final AuthorityService authorityService;

    private final WriterAuthorityService writerAuthorityService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        logger.info("load user by oauth :{}", UserContextHolder.getContext().getCorrelationId());

        OAuth2UserService<OAuth2UserRequest,OAuth2User>oAuthService=new DefaultOAuth2UserService();

        OAuth2User oauth2User=oAuthService.loadUser(oAuth2UserRequest);


        return null;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

    }
}
