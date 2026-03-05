package com.sphere.user.service;

import com.sphere.user.Block;
import com.sphere.user.User;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.repository.BlockRepository;
import com.sphere.user.repository.FollowRepository;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public void blockUser(String username) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User blocker = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User blocked = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (blocker.getId().equals(blocked.getId())) {
            throw new RuntimeException("You cannot block yourself");
        }

        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw new RuntimeException("User already blocked");
        }

        // remove follow relationship if exists
        followRepository.findByFollowerAndFollowing(blocker, blocked)
                .ifPresent(followRepository::delete);
        followRepository.findByFollowerAndFollowing(blocked, blocker)
                .ifPresent(followRepository::delete);

        Block block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        blockRepository.save(block);
    }

    public void unblockUser(String username) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User blocker = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User blocked = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
                .orElseThrow(() -> new RuntimeException("User not blocked"));

        blockRepository.delete(block);
    }

    public List<UserResponse> getBlockedUsers() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User blocker = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return blockRepository.findByBlocker(blocker)
                .stream()
                .map(b -> mapToResponse(b.getBlocked()))
                .collect(Collectors.toList());
    }

    public boolean isBlocked(User blocker, User blocked) {
        return blockRepository.existsByBlockerAndBlocked(blocker, blocked);
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