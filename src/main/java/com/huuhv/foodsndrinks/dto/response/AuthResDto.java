package com.huuhv.foodsndrinks.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResDto {
    private final String accessToken;
    @Builder.Default
    private final String tokenType = "Bearer";
    /** Thời gian sống của token (giây) */
    private final long expiresIn;
    private final String username;
    private final String role;
}
