package com.sphere.auth.service;

import com.sphere.auth.dto.AuthResponse;
import com.sphere.auth.dto.LoginRequest;
import com.sphere.auth.dto.RegisterRequest;
import com.sphere.common.jwt.JwtUtil;
import com.sphere.user.User;
import com.sphere.user.Role;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sphere.auth.service.OtpService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        String defaultAvatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getUsername();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .avatarUrl(defaultAvatar)
                .enabled(false)
                .build();

        userRepository.save(user);
        otpService.sendOtp(request.getEmail());

        return new AuthResponse(null, user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email before logging in!");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }
}