package com.sphere.user.repository;

import com.sphere.user.FollowRequest;
import com.sphere.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    boolean existsByRequesterAndTarget(User requester, User target);
    Optional<FollowRequest> findByRequesterAndTarget(User requester, User target);
    List<FollowRequest> findByTargetOrderByCreatedAtDesc(User target);
}
