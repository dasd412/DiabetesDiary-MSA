package com.dasd412.api.writerservice.application.service.security;

import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.in.security.PrincipalDetails;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.application.service.security.provider.OAuth2UserInfo;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import com.dasd412.api.writerservice.application.service.security.vo.OAuth2UserVO;
import com.dasd412.api.writerservice.application.service.writer.JoinFacadeService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
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
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OAuth2UserInfoFactory factory;

    private final JWTTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieProvider cookieProvider;

    private final JoinFacadeService joinFacadeService;

    private final WriterRepository writerRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        logger.info("load user by oauth :{}", UserContextHolder.getContext().getCorrelationId());

        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuthService = new DefaultOAuth2UserService();

        OAuth2User oauth2User = oAuthService.loadUser(oAuth2UserRequest);

        OAuth2UserInfo oAuth2UserInfo = factory.selectOAuth2UserInfo(oauth2User, oAuth2UserRequest).orElseThrow(() -> new IllegalStateException("provider not supported"));

        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();

        OAuth2UserVO oAuth2UserVO = OAuth2UserVO.builder()
                .name(username)
                .email(oAuth2UserInfo.getEmail())
                .password("none")
                .provider(oAuth2UserInfo.getProvider())
                .providerId(oAuth2UserInfo.getProviderId())
                .build();

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.USER);

        Writer writer = writerRepository.findWriterByName(username)
                .orElseGet(() -> {
                    try {
                        return joinFacadeService.join(oAuth2UserVO, roleSet);
                    } catch (TimeoutException e) {
                        logger.error("too long time in saving new oauth2 user...");
                        throw new RuntimeException(e);
                    }
                });

        return new PrincipalDetails(writer, oauth2User.getAttributes());
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("after authentication success : {}",UserContextHolder.getContext().getCorrelationId());

        PrincipalDetails principalDetails=((PrincipalDetails)authentication.getPrincipal());

        Writer loginUser=principalDetails.getWriter();

        String refreshToken=jwtTokenProvider.issueJwtRefreshToken();

        refreshTokenService.updateRefreshToken(loginUser.getId(),jwtTokenProvider.retrieveRefreshTokenId(refreshToken));

        logger.info("making cookie...");

        ResponseCookie responseCookie=cookieProvider.createCookieForRefreshToken(refreshToken);

        Cookie cookie=cookieProvider.createCookie(responseCookie);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.addCookie(cookie);

        logger.info("making response body...");

        String accessToken = jwtTokenProvider.issueNewJwtAccessToken(principalDetails.getUsername(), principalDetails.getWriter().getId(), request.getRequestURI(), principalDetails.getAuthorities());

        LocalDateTime expired = LocalDateTime.ofInstant(jwtTokenProvider.retrieveExpiredTime(accessToken).toInstant(), ZoneId.systemDefault());

        Map<String, Object> responseBody = Map.of(
                "accessToken", accessToken,
                "expired", expired.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }
}
