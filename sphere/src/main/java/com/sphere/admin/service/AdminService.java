package com.sphere.admin.service;

import com.sphere.community.repository.CommunityRepository;
import com.sphere.post.repository.PostRepository;
import com.sphere.comment.repository.CommentRepository;
import com.sphere.user.User;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(postId);
    }

    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteCommunity(Long communityId) {
        if (!communityRepository.existsById(communityId)) {
            throw new RuntimeException("Community not found");
        }
        communityRepository.deleteById(communityId);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void promoteToModerator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(com.sphere.user.Role.MODERATOR);
        userRepository.save(user);
    }

    public Map<String, Long> getDashboardStats() {
        return Map.of(
                "totalUsers", userRepository.count(),
                "totalPosts", postRepository.count(),
                "totalComments", commentRepository.count(),
                "totalCommunities", communityRepository.count()
        );
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}