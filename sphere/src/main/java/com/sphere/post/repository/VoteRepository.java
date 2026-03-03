package com.sphere.post.repository;

import com.sphere.post.Vote;
import com.sphere.post.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPost_IdAndUser_Id(Long postId, Long userId);
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
}