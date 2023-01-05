package com.dasd412.api.writerservice.adapter.in.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    @Value("${token.access-expired-time}")
    private long ACCESS_EXPIRED_TIME;

    @Value("${token.refresh-expired-time}")
    private long REFRESH_EXPIRED_TIME;

    @Value("${token.secret}")
    private String SECRET;

    private static final String ROLE = "roles";

    private static final String TOKEN_ID = "token_id";

    private static final String USER_ID="user_id";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String issueNewJwtAccessToken(String username, Long writerId,String requestURI, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);

        claims.put(USER_ID,writerId);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put(ROLE, roles);

        return Jwts.builder().addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRED_TIME))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setIssuer(requestURI)
                .compact();
    }

    public String issueJwtRefreshToken() {
        Claims claims = Jwts.claims();
        claims.put(TOKEN_ID, UUID.randomUUID());

        return Jwts.builder().addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRED_TIME))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String retrieveUserId(String token) {
        return retrieveClaimsFromJwtToken(token).getSubject();
    }

    public String retrieveRefreshTokenId(String token) {
        return retrieveClaimsFromJwtToken(token).get(TOKEN_ID).toString();
    }

    public Date retrieveExpiredTime(String token) {
        return retrieveClaimsFromJwtToken(token).getExpiration();
    }

    public List<String> retrieveRoles(String token) {
        return (List<String>) retrieveClaimsFromJwtToken(token).get(ROLE);
    }

    public boolean validateJwtToken(String token) {
        try {
            //parseClaimsJws()으로 해야 UnsupportedJwtException 발생 안함.
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException |
                 UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException exception) {
            logger.error("jwt validation failed : {}", exception.getMessage());
            return false;
        }
    }

    private Claims retrieveClaimsFromJwtToken(String token) {
        try {
            //parseClaimsJws()으로 해야 UnsupportedJwtException 발생 안함.
            return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
