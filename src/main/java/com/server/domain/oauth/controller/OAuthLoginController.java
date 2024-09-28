package com.server.domain.oauth.controller;

import com.server.domain.oauth.dto.GithubDto;
import com.server.domain.oauth.dto.OAuthAccessTokenRequest;
import com.server.domain.oauth.dto.OAuthInfo;
import com.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginController {

    private final UserService userService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;



    /**
     * 깃허브 로그인 인증 시 redirect 되는 것 GET
     * @param code (깃허브가 보내주는 code, 이 code로 accessToken을 요청해야함)
     * @return
     */
    @GetMapping("/login/oauth2/code/github")
    public GithubDto githubLogin(@RequestParam String code) {
        //log.info("sajkfjsakldjklasjdklsajdlkjaslkdja");
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OAuthInfo> response = restTemplate.exchange("https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                getAccessToken(code),
                OAuthInfo.class);
        String accessToken = response.getBody().getAccessToken();

        ResponseEntity<GithubDto> response1 = restTemplate.exchange("https://api.github.com/user"
                , HttpMethod.GET
                , getUserInfo(accessToken)
                , GithubDto.class);

        GithubDto userInfo = response1.getBody();
        log.info("email: "+userInfo.getUsername());
        //userService.login(userInfo, access_token);
        return userInfo;
    }

    private HttpEntity<MultiValueMap<String,String>> getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id",clientId);
        params.add("client_secret",clientSecret);
        params.add("code",code);

        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<>(params,headers);
    }

    /**
     * 로그인 인증 후 받은 code로 post 요청을 보내면 access_token을 응답받는다. 이를 GET 하기 위한 Controller
     * @param access_token
     * @return
     */
    @GetMapping("/githubLogin/success")
    public String githubLoginSuccess(@RequestParam String access_token) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GithubDto> response = restTemplate.exchange("https://api.github.com/user"
                , HttpMethod.GET
                , getUserInfo(access_token)
                , GithubDto.class);

        GithubDto userInfo = response.getBody();
        log.info("email: "+userInfo.getUsername());
        //userService.login(userInfo, access_token);
        return "redirect:/";
    }
    private HttpEntity<MultiValueMap<String,String>> getUserInfo(String access_token) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "token " + access_token);
        return new HttpEntity<>(requestHeaders);
    }
}