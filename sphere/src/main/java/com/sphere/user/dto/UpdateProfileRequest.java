package com.sphere.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String bio;
    private String avatarUrl;
}