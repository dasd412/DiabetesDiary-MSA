package com.dasd412.api.writerservice.application.service.vo;

import com.dasd412.api.writerservice.domain.writer.Writer;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

public class UserDetailsVO implements AuthenticationVO {

    private final String name;

    private final String email;

    private final String password;

    @Builder
    public UserDetailsVO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public Writer makeEntityWithPasswordEncode(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Writer.builder()
                .name(this.name)
                .email(this.email)
                .password(bCryptPasswordEncoder.encode(this.password))
                .provider(null)
                .providerId(null)
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDetailsVO target = (UserDetailsVO) obj;
        return Objects.equals(this.name, target.name)
                && Objects.equals(this.email, target.email)
                && Objects.equals(this.password, target.password);
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
        return null;
    }

    @Override
    public String getProviderId() {
        return null;
    }
}
