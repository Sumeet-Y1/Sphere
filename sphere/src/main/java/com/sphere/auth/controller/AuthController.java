package com.sphere.auth.controller;

import com.sphere.auth.dto.AuthResponse;
import com.sphere.auth.dto.LoginRequest;
import com.sphere.auth.dto.RegisterRequest;
import com.sphere.auth.service.AuthService;
import com.sphere.auth.service.OtpService;
import com.sphere.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.sphere.user.AuthProvider;
import com.sphere.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 👇 dedicated admin login — checks ROLE_ADMIN before issuing token
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody EmailRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            if (user.getAuthProvider() == AuthProvider.GOOGLE) {
                throw new RuntimeException("This account uses Google Sign-In. Please login with Google!");
            }
        });
        otpService.sendPasswordResetOtp(request.email());
        return ResponseEntity.ok(new ApiResponse(true, "Password reset OTP sent to your email!", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully!", null));
    }

    record EmailRequest(String email) {}
    record ResetPasswordRequest(String email, String code, String newPassword) {}
}