package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class OAuthInfo {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long accessTokenExp;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExp;
}
