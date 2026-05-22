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
 
@Service  
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Username already taken");
        }

        String defaultAvatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=" + username;

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .avatarUrl(defaultAvatar)
                .enabled(false)
                .build();

        userRepository.save(user);
        otpService.sendOtp(email);

        return new AuthResponse(null, user.getUsername(), user.getEmail(), user.getRole().name(),
                user.getAvatarUrl(), user.getAuthProvider().name(), user.getTheme());
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier().trim();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
        );

        User user = findByLoginIdentifier(identifier);

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email before logging in!");
        }

        if (user.isBanned()) {
            throw new RuntimeException("Your account has been banned!");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name(),
                user.getAvatarUrl(), user.getAuthProvider().name(), user.getTheme());
    }

    public AuthResponse adminLogin(LoginRequest request) {
        String identifier = request.getIdentifier().trim();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
        );

        User user = findByLoginIdentifier(identifier);

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified!");
        }

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied! Admins only.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name(),
                user.getAvatarUrl(), user.getAuthProvider().name(), user.getTheme());
    }

    public void resetPassword(String email, String code, String newPassword) {
        otpService.verifyOtp(email, code);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User findByLoginIdentifier(String identifier) {
        return userRepository.findByEmailIgnoreCase(identifier)
                .or(() -> userRepository.findByUsernameIgnoreCase(identifier))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
