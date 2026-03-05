package com.sphere.user.controller;

import com.sphere.user.dto.FollowResponse;
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
    public ResponseEntity<FollowResponse> followUser(@PathVariable String username) {
        return ResponseEntity.ok(followService.followUser(username));
    }

    @DeleteMapping("/{username}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable String username) {
        followService.unfollowUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowers(username));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<UserResponse>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowing(username));
    }
}