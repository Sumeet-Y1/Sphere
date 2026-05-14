package com.sphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private String role;
    private String authProvider;
    private boolean banned;
    private int followersCount;
    private int followingCount;
    private boolean privateAccount;
    private boolean following;
    private boolean requestedFollow;
    private boolean canViewProfile;
    private boolean ownProfile;
    private LocalDateTime createdAt;
}
