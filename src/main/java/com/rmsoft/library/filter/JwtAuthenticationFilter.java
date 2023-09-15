package com.rmsoft.library.filter;

import com.rmsoft.library.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Request가 들어왔을 때 Request Header의 Authorization 필드의 Bearer Token을 가져온다.
    // 가져온 토큰을 검증하고 검증 결과를 SecurityContext에 추가 (스레드 내에서 지속적으로 유지목적)

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseBearerToken(request);
            if(token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 검증해서 payload의 userId 가져온다.
            String userId = jwtProvider.validate(token);
            if(userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // SecurityContext에 추가할 객체
            AbstractAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext에 AbstractAuthenticationToken 객체를 추가해서
            // 해당 Thread가 지속적으로 인증 정보를 가질 수 있도록 처리

            // 빈 컨텍스트 생성
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticationToken); //token 세팅
            // SecurityContextHolder에 token이 세팅된 컨텍스트를 등록
            SecurityContextHolder.setContext(securityContext);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // 추가를 해야지 다음 처리과정이 진행됨
        // 안할 시 여기서 끝난다.
        filterChain.doFilter(request, response);

        // 만들어진 인증 필터를 Config에 등록 [ WebSecurityConfig ]
    }

    // Request Header의 Authorization 필드의 Bearer Token을 가져오는 메서드
    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        // 권한 필드 유무 체크 && Bearer 토큰인지 체크
        if(!StringUtils.hasText(authorization)) return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) return null;

        return  authorization.substring(7); //7번째부터 토큰 값
    }
}
