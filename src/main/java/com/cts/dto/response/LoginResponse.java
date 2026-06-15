package com.cts.dto.response;

import com.cts.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;   // "Bearer"
    private Long userId;
    private Role role;
    private Long siteId;
}