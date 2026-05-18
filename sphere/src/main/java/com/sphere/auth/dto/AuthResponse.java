package com.sphere.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private String authProvider;
    private String theme;
}
