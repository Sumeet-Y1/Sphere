package com.sphere.user.controller;

import com.sphere.user.dto.UpdateProfileRequest;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sphere.common.storage.S3Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @PostMapping("/me/avatar")
    public ResponseEntity<UserResponse> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String avatarUrl = s3Service.uploadFile(file, "avatars");
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl(avatarUrl);
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<UserResponse> removeAvatar() {
        String defaultAvatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=";

        // get current user
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        com.sphere.user.dto.UpdateProfileRequest request = new com.sphere.user.dto.UpdateProfileRequest();

        // delete from S3 if not default avatar
        com.sphere.user.User user = userService.getUserByEmail(email);
        if (user.getAvatarUrl() != null && user.getAvatarUrl().contains("amazonaws.com")) {
            try {
                s3Service.deleteFile(user.getAvatarUrl());
            } catch (Exception e) {
                // ignore if delete fails
            }
        }

        request.setAvatarUrl(defaultAvatar + user.getUsername());
        return ResponseEntity.ok(userService.updateProfile(request));
    }

}