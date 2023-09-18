package com.rmsoft.library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT: 전자 서명이 된 토큰
 * JSON 형태로 구성된 토큰
 * [header].[payload].[signature]
 * header: type (해당 토큰의 타입), alg (토큰을 서명하기 위해 사용된 해시 알고리즘)
 * payload: sub (해당 토큰의 주인), iat (토큰이 발행된 시간), exp (토큰이 만료되는 시간)
 */

@Slf4j
@Component
public class JwtProvider {

    // JWT 생성 메서드
    // JWT 생성 및 검증을 위한 키
    @Value("${secret-key}")
    private String SECURITY_KEY;

    public String create (String userId) {
        // 만료날짜를 현재 날짜 + 1시간
        Date expiredTime = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));


        // JWT를 생성
        return Jwts.builder()
                // 암호화에 사용될 알고리즘, 키
                .signWith(SignatureAlgorithm.HS512, SECURITY_KEY)
                //JWT 제목, 생성일, 만료일
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(expiredTime)
                //생성
                .compact();
    }

    // 인증용 토큰
    // JWT 검증
    public String validate (String jwtToken) {
        // 매개변수로 받은 token을 키를 사용해서 복호화 (디코딩)
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECURITY_KEY).parseClaimsJws(jwtToken).getBody();
        } catch(Exception exception) {
//            log.info("JwtProvider validate: " + exception.getMessage());
            return null;
        }

        // 복호화된 토큰의 payload에서 제목을 가져옴
        return claims.getSubject(); //지정한 userId 가져옴
    }
}
