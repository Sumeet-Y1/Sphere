package com.sphere.user.service;

import com.sphere.notifications.service.NotificationService;
import com.sphere.user.Follow;
import com.sphere.user.User;
import com.sphere.user.dto.FollowResponse;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.repository.FollowRepository;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowResponse followUser(String username) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User following = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        notificationService.sendNotification(
                following.getUsername(),
                "FOLLOW",
                follower.getUsername() + " started following you",
                follower.getId()
        );

        return mapToResponse(follow);
    }

    public void unfollowUser(String username) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User following = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("Not following"));

        followRepository.delete(follow);
    }

    public List<UserResponse> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.findByFollowing(user)
                .stream()
                .map(f -> mapUserToResponse(f.getFollower()))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.findByFollower(user)
                .stream()
                .map(f -> mapUserToResponse(f.getFollowing()))
                .collect(Collectors.toList());
    }

    private FollowResponse mapToResponse(Follow follow) {
        return FollowResponse.builder()
                .id(follow.getId())
                .followerUsername(follow.getFollower().getUsername())
                .followingUsername(follow.getFollowing().getUsername())
                .createdAt(follow.getCreatedAt())
                .build();
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}