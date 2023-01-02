package com.dasd412.api.writerservice.adapter.in.security.auth;

import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PrincipalDetails implements UserDetails {

    //todo 나중에 OAUTH도 구현하자.

    //실제 엔티티 참조
    private final Writer writer;

    //작성자가 가질 수 있는 권한 엔티티 목록
    private List<Authority> authorities;

    public PrincipalDetails(Writer writer) {
        this.writer = writer;
    }

    public PrincipalDetails(Writer writer, List<Authority> authorities) {
        this.writer = writer;
        this.authorities = authorities;
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
}
