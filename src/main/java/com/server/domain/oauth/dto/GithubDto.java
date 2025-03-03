package com.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class GithubDto {
    @JsonProperty("login")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("avatar_url")
    private String thumbnail;

}
