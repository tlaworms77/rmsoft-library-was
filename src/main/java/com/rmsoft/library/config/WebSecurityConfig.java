package com.rmsoft.library.config;

import com.rmsoft.library.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity //사용하는 WebSecurity로 설정
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // cors 정책 (현재는 Application에서 작업을 했으므로 기본 설정 사용)
                .cors().and()
                // csrf 대책 : 취약점 대책 설정 (현재는 CSRF에 대한 대책을 비활성)
                .csrf().disable()
                // Basic 인증 (현재는 Bearer token 인증방법을 사용하기 때문에 비활성)
                .httpBasic().disable()
                // 세션 기반 인증 (현재는 Session 기반 인증을 사용하지 않기 때문에 비활성)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // '/', '/api/auth' 모듈에 대해서는 모두 허용 (인증을 하지 않고 사용 가능)
                .authorizeHttpRequests().antMatchers("/", "/api/auth/**", "/outputs/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/book/**").permitAll()
                // 나머지 Request요청에 대해서는 모두 인증된 사용자만 사용가능하도록 처리
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(new FailedAuthenticationEntryPoint());

        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
//
//        httpSecurity
//                .cors(Customizer.withDefaults())
//                .csrf((csrf) -> csrf.disable())
//                .httpBasic((httpBasic) -> httpBasic.disable())
//                .sessionManagement((sessionManagement) ->            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.antMatchers("/", "/api/auth/**").permitAll().anyRequest().authenticated());
//
//        httpSecurity.addFilterBefore(jwtAuthencationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return httpSecurity.build();
    }

    class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{ \"code\": \"NP\", \"message\": \"Do not have permission.\" }");
        }
    }

}
