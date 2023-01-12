package com.dasd412.api.writerservice.adapter.in.security;

import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrincipalDetails implements UserDetails, OAuth2User {

    //실제 엔티티 참조
    private final Writer writer;

    //작성자가 가질 수 있는 권한 엔티티 목록
    private List<Authority> authorities;

    /**
     * OAuth와 관련된 속성을 담고 있는 해시맵. OAuth 로그인시에만 쓰인다.
     */
    private Map<String, Object> oauthAttributes;

    public PrincipalDetails(Writer writer) {
        this.writer = writer;
    }

    public PrincipalDetails(Writer writer, Map<String, Object> oauthAttributes) {
        this.writer = writer;
        this.oauthAttributes = oauthAttributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.oauthAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        if (this.authorities == null) {

            collection.add((GrantedAuthority) Role.USER::name);

        } else {
            List<String> roles = authorities.stream().map(authority -> authority.getRole().name()).collect(Collectors.toList());

            roles.forEach(role -> {
                collection.add((GrantedAuthority) () -> role);
            });
        }

        return collection;
    }

    @Override
    public String getPassword() {
        return writer.getPassword();
    }

    @Override
    public String getUsername() {
        return writer.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public String getName() {
        return null;
    }
}
