package com.sphere.user.service;

import com.sphere.user.User;
import com.sphere.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPrivacyService {

    private final FollowRepository followRepository;

    public boolean canViewProfile(User viewer, User target) {
        if (target == null) return false;
        if (!target.isPrivateAccount()) return true;
        if (viewer == null) return false;
        if (viewer.getId().equals(target.getId())) return true;
        return followRepository.existsByFollowerAndFollowing(viewer, target);
    }

    public void assertCanViewProfile(User viewer, User target) {
        if (!canViewProfile(viewer, target)) {
            throw new RuntimeException("This account is private");
        }
    }
}
