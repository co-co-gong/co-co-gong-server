package com.server.domain.oauth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.server.domain.oauth.dto.GithubDto;
import com.server.domain.oauth.dto.OAuthInfo;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.dto.TokenDto;
import com.server.global.jwt.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginController {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    // 새로 추가된 로그인 시작점
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String githubAuthUrl = String.format("%s%s%s%s%s", "https://github.com/login/oauth/authorize?client_id=",
                clientId, "&redirect_uri=", redirectUri, "/login/oauth2/code/github");
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, githubAuthUrl)
                .build();
    }

    @GetMapping("/login/oauth2/code/github")
    public ResponseEntity<?> githubLogin(@RequestParam String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // GitHub 액세스 토큰 요청
            ResponseEntity<OAuthInfo> response = restTemplate.exchange(
                    "https://github.com/login/oauth/access_token",
                    HttpMethod.POST,
                    getAccessToken(code),
                    OAuthInfo.class);
            String githubAccessToken = response.getBody().getAccessToken();

            // GitHub 사용자 정보 요청
            ResponseEntity<GithubDto> userInfoResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    getUserInfo(githubAccessToken),
                    GithubDto.class);

            GithubDto githubDto = userInfoResponse.getBody();
            log.info("Received user info. Username: " + githubDto.getUsername());

            // 사용자 등록 또는 로그인 처리
            User user = userService.loginOrRegister(githubDto, githubAccessToken);

            // JWT 토큰 생성
            String accessToken = jwtService.createAccessToken(user.getUsername());
            String refreshToken = jwtService.createRefreshToken(user.getUsername());

            // Refresh 토큰 저장
            userService.saveRefreshToken(user.getUsername(), refreshToken);

            // 응답 생성
            TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
            ApiResponseDto<TokenDto> responseDto = ApiResponseDto.success(HttpStatus.OK.value(), tokenDto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .body(responseDto);
        } catch (Exception e) {
            log.error("Error during GitHub OAuth process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "An error occurred during the OAuth process"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();

        if (jwtService.validateToken(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken).get();
            Optional<User> user = userService.findByUsername(username);
            if (user.isPresent()) {
                if (refreshToken.equals(user.get().getRefreshToken())) {
                    String newAccessToken = jwtService.createAccessToken(username);
                    TokenDto newTokenDto = new TokenDto(newAccessToken, refreshToken);
                    return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), newTokenDto));
                }
            }

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token"));
    }

    private HttpEntity<MultiValueMap<String, String>> getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(params, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }
}
