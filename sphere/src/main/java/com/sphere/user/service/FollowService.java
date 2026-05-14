package com.sphere.user.service;

import com.sphere.notifications.service.NotificationService;
import com.sphere.user.Follow;
import com.sphere.user.FollowRequest;
import com.sphere.user.User;
import com.sphere.user.dto.FollowResponse;
import com.sphere.user.dto.FollowRequestResponse;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.repository.FollowRepository;
import com.sphere.user.repository.FollowRequestRepository;
import com.sphere.user.repository.UserRepository;
import com.sphere.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ApiResponse<?> followUser(String username) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User following = userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following");
        }

        if (followRequestRepository.existsByRequesterAndTarget(follower, following)) {
            throw new RuntimeException("Follow request already sent");
        }

        if (following.isPrivateAccount()) {
            FollowRequest followRequest = FollowRequest.builder()
                    .requester(follower)
                    .target(following)
                    .build();
            followRequestRepository.save(followRequest);
            return new ApiResponse<>(true, "Follow request sent", null);
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

        return new ApiResponse<>(true, "Followed successfully", mapToResponse(follow));
    }

    public ApiResponse<?> unfollowUser(String username) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User following = userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElse(null);

        if (follow != null) {
            followRepository.delete(follow);
            return new ApiResponse<>(true, "Unfollowed successfully", null);
        }

        FollowRequest followRequest = followRequestRepository.findByRequesterAndTarget(follower, following)
                .orElseThrow(() -> new RuntimeException("Not following"));

        followRequestRepository.delete(followRequest);
        return new ApiResponse<>(true, "Follow request cancelled", null);
    }

    public List<FollowRequestResponse> getPendingFollowRequests() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User target = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRequestRepository.findByTargetOrderByCreatedAtDesc(target)
                .stream()
                .map(this::mapFollowRequestToResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse<?> acceptFollowRequest(Long requestId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User target = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FollowRequest followRequest = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));

        if (!followRequest.getTarget().getId().equals(target.getId())) {
            throw new RuntimeException("You cannot manage this follow request");
        }

        if (!followRepository.existsByFollowerAndFollowing(followRequest.getRequester(), target)) {
            Follow follow = Follow.builder()
                    .follower(followRequest.getRequester())
                    .following(target)
                    .build();
            followRepository.save(follow);
        }

        followRequestRepository.delete(followRequest);
        return new ApiResponse<>(true, "Follow request accepted", null);
    }

    public ApiResponse<?> rejectFollowRequest(Long requestId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User target = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FollowRequest followRequest = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));

        if (!followRequest.getTarget().getId().equals(target.getId())) {
            throw new RuntimeException("You cannot manage this follow request");
        }

        followRequestRepository.delete(followRequest);
        return new ApiResponse<>(true, "Follow request rejected", null);
    }

    public List<UserResponse> getFollowers(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.findByFollowing(user)
                .stream()
                .map(f -> mapUserToResponse(f.getFollower()))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getFollowing(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
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
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .privateAccount(user.isPrivateAccount())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private FollowRequestResponse mapFollowRequestToResponse(FollowRequest followRequest) {
        return FollowRequestResponse.builder()
                .id(followRequest.getId())
                .requesterUsername(followRequest.getRequester().getUsername())
                .requesterAvatarUrl(followRequest.getRequester().getAvatarUrl())
                .requesterBio(followRequest.getRequester().getBio())
                .createdAt(followRequest.getCreatedAt())
                .build();
    }
}
