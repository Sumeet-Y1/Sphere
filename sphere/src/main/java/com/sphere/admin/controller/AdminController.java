package com.sphere.admin.controller;

import com.sphere.admin.service.AdminService;
import com.sphere.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/promote")
    public ResponseEntity<Void> promoteToModerator(@PathVariable Long userId) {
        adminService.promoteToModerator(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/communities/{communityId}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable Long communityId) {
        adminService.deleteCommunity(communityId);
        return ResponseEntity.noContent().build();
    }
}