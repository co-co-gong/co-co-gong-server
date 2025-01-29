package com.server.domain.oauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.oauth.service.OAuthLoginService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.TokenDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginController {

    private final OAuthLoginService oAuthLoginService;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    // 새로 추가된 로그인 시작점
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/login")
    @Operation(summary = "로그인", description = "github oauth url로 연결")
    public ApiResponseDto<String> login(HttpServletResponse response) {
        // NOTE: redirect_uri는 선택적으로 사용할 수 있고, 미지정 시 GitHub OAuth App에서 설정한 값으로 이동한다.
        String githubAuthUrl = String.format("https://github.com/login/oauth/authorize?client_id=%s&scope=repo,user",
                clientId);
        response.setHeader(HttpHeaders.LOCATION, githubAuthUrl);
        return ApiResponseDto.success(HttpStatus.FOUND.value(), "Login Success");
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/login/oauth2/github")
    @Operation(summary = "github oauth 로그인", description = "/login 호출 시 자동 호출")
    public ApiResponseDto<TokenDto> githubLogin(HttpServletResponse response, @RequestParam String code) {
        // github 로그인 후 토큰 발급
        TokenDto tokenDto = oAuthLoginService.processGithubLogin(code);

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken());
        response.setHeader(HttpHeaders.LOCATION,
                String.format("%s/auth/github/callback?accessToken=%s&refreshToken=%s",
                        redirectUri, tokenDto.getAccessToken(), tokenDto.getRefreshToken()));
        return ApiResponseDto.success(HttpStatus.FOUND.value(), tokenDto);

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    @Operation(summary = "refresh 토큰 발급", description = "Token Dto을 입력받아 refresh token 제공")
    public ApiResponseDto<TokenDto> refreshToken(@RequestBody TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();
        log.info(refreshToken);

        TokenDto newTokenDto = oAuthLoginService.refresh(refreshToken);

        return ApiResponseDto.success(HttpStatus.OK.value(), newTokenDto);
    }

}
