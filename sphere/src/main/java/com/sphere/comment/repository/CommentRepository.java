package com.sphere.comment.repository;

import com.sphere.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_IdAndParentIsNullOrderByCreatedAtDesc(Long postId);
    List<Comment> findByParent_IdOrderByCreatedAtDesc(Long parentId);
    int countByPost_Id(Long postId);
}