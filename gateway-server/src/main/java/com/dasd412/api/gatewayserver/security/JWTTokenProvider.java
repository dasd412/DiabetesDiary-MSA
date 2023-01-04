package com.dasd412.api.gatewayserver.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public String createJwtAccessToken(String username, Long writerId,String requestURI, List<String>roles) {
        Claims claims = Jwts.claims().setSubject(username);

        claims.put(USER_ID,writerId);

        claims.put(ROLE, roles);

        return Jwts.builder().addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRED_TIME))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setIssuer(requestURI)
                .compact();
    }

    public String createJwtRefreshToken() {
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

    private Claims retrieveClaimsFromJwtToken(String token) {
        try {
            //parseClaimsJws()으로 해야 UnsupportedJwtException 발생 안함.
            return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}