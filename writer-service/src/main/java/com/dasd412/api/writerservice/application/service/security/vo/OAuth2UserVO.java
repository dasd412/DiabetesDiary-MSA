package com.dasd412.api.writerservice.application.service.security.vo;

import com.dasd412.api.writerservice.domain.writer.Writer;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

public class OAuth2UserVO implements AuthenticationVO {

    private final String name;

    private final String email;

    private final String password;

    private final String provider;

    private final String providerId;

    @Builder
    public OAuth2UserVO(String name, String email, String password, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public Writer makeEntityWithPasswordEncode(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Writer.builder()
                .name(this.name)
                .email(this.email)
                .password(bCryptPasswordEncoder.encode(this.password))
                .provider(this.provider)
                .providerId(this.providerId)
                .build();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getProvider() {
        return this.provider;
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, provider, providerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OAuth2UserVO target = (OAuth2UserVO) obj;
        return Objects.equals(this.name, target.name)
                && Objects.equals(this.email, target.email)
                && Objects.equals(this.password, target.password)
                && Objects.equals(this.provider, target.provider)
                && Objects.equals(this.providerId, target.providerId);
    }
}
