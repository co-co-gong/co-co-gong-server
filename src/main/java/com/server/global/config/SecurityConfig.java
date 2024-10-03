package com.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // == URL별 권한 관리 옵션 ==//
                // 아이콘, css, js 관련
                // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api*", "/api-docs/**", "swagger/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/auth/**", "/login/**", "/oauth2/**", "/signaling/**").permitAll()
                        .requestMatchers("githubLogin/**").permitAll()
                        .anyRequest().authenticated() // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능
                )
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 비활성화
                .formLogin(form -> form.disable()) // Form 로그인 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // FrameOptions 비활성화
                // 세션 사용하지 않으므로 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
