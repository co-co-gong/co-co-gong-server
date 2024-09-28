package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthAccessTokenRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;
    private String code;

    public OAuthAccessTokenRequest(final String clientId, final String clientSecret, final String code) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.code = code;
    }

}