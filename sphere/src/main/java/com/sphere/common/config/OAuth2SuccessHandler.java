package com.sphere.common.config;

import com.sphere.common.jwt.JwtUtil;
import com.sphere.user.Role;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // find or create user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String username = generateUsername(name);
            return userRepository.save(User.builder()
                    .email(email)
                    .username(username)
                    .password("")
                    .role(Role.USER)
                    .avatarUrl(picture)
                    .enabled(true)
                    .build());
        });

        // generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // redirect to frontend with token
        response.sendRedirect("http://localhost:5173/oauth2/callback?token=" + token);
    }

    private String generateUsername(String name) {
        String base = name.toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-z0-9]", "");
        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + counter++;
        }
        return username;
    }
}