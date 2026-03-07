package com.sphere.auth.controller;

import com.sphere.auth.service.OtpService;
import com.sphere.common.response.ApiResponse;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final UserRepository userRepository;

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody OtpRequest request) {
        otpService.verifyOtp(request.email(), request.code());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully!", null));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestBody EmailRequest request) {
        otpService.sendOtp(request.email());
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP resent successfully!", null));
    }

    record OtpRequest(String email, String code) {}
    record EmailRequest(String email) {}
}