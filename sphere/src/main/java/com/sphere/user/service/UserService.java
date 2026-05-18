package com.sphere.user.service;

import com.sphere.user.User;
import com.sphere.user.dto.UpdateProfileRequest;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.repository.BlockRepository;
import com.sphere.user.repository.FollowRepository;
import com.sphere.user.repository.FollowRequestRepository;
import com.sphere.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final List<String> ALLOWED_THEMES = List.of("dark", "dim", "light");

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final UserPrivacyService userPrivacyService;

    public UserResponse getProfile(String username) {
        User viewer = getCurrentUser();
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user, viewer);
    }

    public UserResponse getMyProfile() {
        User user = getCurrentUser();
        return mapToResponse(user, user);
    }

    public UserResponse updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null) {
            String normalizedUsername = request.getUsername().trim();
            if (normalizedUsername.isEmpty()) {
                throw new RuntimeException("Username cannot be empty");
            }
            if (!normalizedUsername.equals(user.getUsername()) && userRepository.existsByUsername(normalizedUsername)) {
                throw new RuntimeException("Username is already taken");
            }

            boolean shouldRefreshDefaultAvatar = user.getAvatarUrl() == null
                    || user.getAvatarUrl().contains("api.dicebear.com/7.x/avataaars/svg?seed=");

            user.setUsername(normalizedUsername);

            if (shouldRefreshDefaultAvatar && request.getAvatarUrl() == null) {
                user.setAvatarUrl(defaultAvatarUrl(normalizedUsername));
            }
        }
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getPrivateAccount() != null) user.setPrivateAccount(request.getPrivateAccount());
        if (request.getTheme() != null) user.setTheme(normalizeTheme(request.getTheme()));

        userRepository.save(user);
        return mapToResponse(user, user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void changePassword(String currentPassword, String newPassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        execute("""
                UPDATE communities c
                JOIN (
                    SELECT DISTINCT community_id
                    FROM community_members
                    WHERE user_id = :userId
                ) memberships ON memberships.community_id = c.id
                SET c.member_count = CASE WHEN c.member_count > 0 THEN c.member_count - 1 ELSE 0 END
                """, userId);

        execute("DELETE FROM messages WHERE conversation_id IN (SELECT id FROM conversations WHERE user1_id = :userId OR user2_id = :userId)", userId);
        execute("DELETE FROM messages WHERE sender_id = :userId", userId);
        execute("DELETE FROM conversations WHERE user1_id = :userId OR user2_id = :userId", userId);

        execute("DELETE FROM blocks WHERE blocker_id = :userId OR blocked_id = :userId", userId);
        execute("DELETE FROM follows WHERE follower_id = :userId OR following_id = :userId", userId);
        execute("DELETE FROM community_members WHERE user_id = :userId", userId);
        execute("DELETE FROM community_members WHERE community_id IN (SELECT id FROM communities WHERE owner_id = :userId)", userId);

        execute("""
                DELETE FROM votes
                WHERE user_id = :userId
                   OR post_id IN (
                       SELECT id FROM posts
                       WHERE author_id = :userId
                          OR community_id IN (SELECT id FROM communities WHERE owner_id = :userId)
                   )
                """, userId);

        execute("""
                DELETE FROM comments
                WHERE parent_id IN (
                    SELECT id FROM (
                        SELECT id FROM comments
                        WHERE author_id = :userId
                           OR post_id IN (
                               SELECT id FROM posts
                               WHERE author_id = :userId
                                  OR community_id IN (SELECT id FROM communities WHERE owner_id = :userId)
                           )
                    ) doomed_comments
                )
                """, userId);
        execute("""
                DELETE FROM comments
                WHERE author_id = :userId
                   OR post_id IN (
                       SELECT id FROM posts
                       WHERE author_id = :userId
                          OR community_id IN (SELECT id FROM communities WHERE owner_id = :userId)
                   )
                """, userId);

        execute("""
                DELETE FROM post_media
                WHERE post_id IN (
                    SELECT id FROM posts
                    WHERE author_id = :userId
                       OR community_id IN (SELECT id FROM communities WHERE owner_id = :userId)
                )
                """, userId);
        execute("""
                DELETE FROM posts
                WHERE author_id = :userId
                   OR community_id IN (SELECT id FROM communities WHERE owner_id = :userId)
                """, userId);

        execute("DELETE FROM communities WHERE owner_id = :userId", userId);
        execute("DELETE FROM otps WHERE email = :email", "email", email);
        userRepository.delete(user);
    }

    private void execute(String sql, Long userId) {
        execute(sql, "userId", userId);
    }

    private void execute(String sql, String paramName, Object paramValue) {
        entityManager.createNativeQuery(sql)
                .setParameter(paramName, paramValue)
                .executeUpdate();
    }

    public List<UserResponse> getBlockedUsers() {
        User user = getCurrentUser();
        return blockRepository.findByBlocker(user)
                .stream()
                .map(block -> mapToResponse(block.getBlocked(), user))
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse mapToResponse(User user, User viewer) {
        boolean ownProfile = viewer != null && viewer.getId().equals(user.getId());
        boolean following = viewer != null
                && !ownProfile
                && followRepository.existsByFollowerAndFollowing(viewer, user);
        boolean requestedFollow = viewer != null
                && !ownProfile
                && followRequestRepository.existsByRequesterAndTarget(viewer, user);
        boolean canViewProfile = userPrivacyService.canViewProfile(viewer, user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(ownProfile ? user.getEmail() : null)
                .bio(canViewProfile ? user.getBio() : null)
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .authProvider(user.getAuthProvider().name())
                .banned(user.isBanned())
                .followersCount(followRepository.countByFollowing(user))
                .followingCount(followRepository.countByFollower(user))
                .privateAccount(user.isPrivateAccount())
                .theme(normalizeTheme(user.getTheme()))
                .following(following)
                .requestedFollow(requestedFollow)
                .canViewProfile(canViewProfile)
                .ownProfile(ownProfile)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String defaultAvatarUrl(String username) {
        return "https://api.dicebear.com/7.x/avataaars/svg?seed=" + Objects.requireNonNull(username);
    }

    private String normalizeTheme(String theme) {
        if (theme == null) {
            return "dark";
        }

        String normalizedTheme = theme.trim().toLowerCase();
        if (!ALLOWED_THEMES.contains(normalizedTheme)) {
            throw new RuntimeException("Unsupported theme");
        }

        return normalizedTheme;
    }

}
