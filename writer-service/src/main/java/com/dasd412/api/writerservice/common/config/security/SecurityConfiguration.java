package com.dasd412.api.writerservice.common.config.security;

import com.dasd412.api.writerservice.adapter.in.security.filter.JwtAuthenticationFilter;
import com.dasd412.api.writerservice.adapter.in.security.jwt.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.out.web.cookie.CookieProvider;
import com.dasd412.api.writerservice.application.service.security.refresh.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    private final JWTTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieProvider cookieProvider;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**");
    }

    /*
    1.JWT는 무상태성이므로 세션이 없고
    2.크로스 오리진을 모두 허용하며
    3.폼로그인을 사용하지 않고
    4.기본 HTTP 방식을 사용하지 않는다고 명시

    JWT 검증은 Gateway server에서 적용하므로, 여기서는 모든 요청 허용
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter authenticationFilter=new JwtAuthenticationFilter(authenticationManagerBean(),jwtTokenProvider,refreshTokenService,cookieProvider);
        authenticationFilter.setFilterProcessesUrl("/login");

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)
                .addFilter(authenticationFilter)
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .csrf().disable();

    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
