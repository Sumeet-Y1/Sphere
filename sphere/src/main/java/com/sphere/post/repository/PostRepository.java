package com.sphere.post.repository;

import com.sphere.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCommunity_IdOrderByCreatedAtDesc(Long communityId);
    List<Post> findByAuthor_IdOrderByCreatedAtDesc(Long authorId);
    List<Post> findAllByOrderByCreatedAtDesc();
}