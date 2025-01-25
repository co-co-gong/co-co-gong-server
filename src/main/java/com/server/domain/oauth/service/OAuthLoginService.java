package com.server.domain.oauth.service;

import com.server.domain.oauth.dto.GithubDto;
import com.server.domain.oauth.dto.OAuthInfo;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.TokenDto;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.error.exception.BusinessException;
import com.server.global.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

    private final UserService userService;
    private final JwtService jwtService;


    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;


    public TokenDto processGithubLogin(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // GitHub 액세스 토큰 요청
            ResponseEntity<OAuthInfo> githubAccessTokenResponse = restTemplate.exchange(
                    "https://github.com/login/oauth/access_token",
                    HttpMethod.POST,
                    getAccessToken(code),
                    OAuthInfo.class);
            String githubAccessToken = githubAccessTokenResponse.getBody().getAccessToken();

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
            return new TokenDto(accessToken, refreshToken);
        }catch (Exception e){
            throw new AuthException(AuthErrorCode.OAUTH_PROCESS_ERROR);
        }

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

    public TokenDto refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtService.extractUsername(refreshToken)
                .orElseThrow(()-> new BusinessException(UserErrorCode.NOT_FOUND));
        User user = userService.getUserWithPersonalInfo(username);

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtService.createAccessToken(username);
        return new TokenDto(newAccessToken, refreshToken);

    }
}
