package com.dasd412.api.writerservice.common.config.security;

import com.dasd412.api.writerservice.adapter.in.security.filter.JwtAuthenticationFilter;
import com.dasd412.api.writerservice.adapter.in.security.jwt.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    private final JWTTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /*
    1.JWT는 무상태성이므로 세션이 없고
    2.크로스 오리진을 모두 허용하며
    3.폼로그인을 사용하지 않고
    4.기본 HTTP 방식을 사용하지 않는다고 명시

    JWT 검증은 Gateway server에서 적용하므로, 여기서는 모든 요청 허용
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .csrf().disable();

        /*
        formLogin().disable()을 사용하면 UsernamePasswordAuthenticationFilter를 이용할 수 없다.
        이를 이용하려면 UsernamePasswordAuthenticationFilter 를 상속한 커스텀 필터를 만들어서 적용해야 한다.
         */
        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean(), jwtTokenProvider));
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}