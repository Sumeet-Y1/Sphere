package com.sphere.admin.controller;

import com.sphere.common.jwt.JwtUtil;
import com.sphere.common.response.ApiResponse;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAccessController {

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/access")
    public ResponseEntity<ApiResponse> adminAccess(@RequestParam String key) {
        if (!key.equals(adminSecretKey)) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Invalid admin key!", null));
        }

        User adminUser = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equals("ADMIN"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No admin user found!"));

        String token = jwtUtil.generateToken(adminUser.getEmail());

        return ResponseEntity.ok(new ApiResponse(true, "Admin access granted!", token));
    }
}