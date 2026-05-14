package com.sphere.user.controller;

import com.sphere.common.response.ApiResponse;
import com.sphere.user.dto.FollowRequestResponse;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{username}/follow")
    public ResponseEntity<ApiResponse<?>> followUser(@PathVariable String username) {
        return ResponseEntity.ok(followService.followUser(username));
    }

    @DeleteMapping("/{username}/unfollow")
    public ResponseEntity<ApiResponse<?>> unfollowUser(@PathVariable String username) {
        return ResponseEntity.ok(followService.unfollowUser(username));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowers(username));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<UserResponse>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowing(username));
    }

    @GetMapping("/me/follow-requests")
    public ResponseEntity<List<FollowRequestResponse>> getPendingFollowRequests() {
        return ResponseEntity.ok(followService.getPendingFollowRequests());
    }

    @PostMapping("/me/follow-requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<?>> acceptFollowRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(followService.acceptFollowRequest(requestId));
    }

    @PostMapping("/me/follow-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<?>> rejectFollowRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(followService.rejectFollowRequest(requestId));
    }
}
