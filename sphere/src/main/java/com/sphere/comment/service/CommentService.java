package com.sphere.comment.service;

import com.sphere.comment.Comment;
import com.sphere.comment.dto.CommentResponse;
import com.sphere.comment.dto.CreateCommentRequest;
import com.sphere.comment.repository.CommentRepository;
import com.sphere.post.Post;
import com.sphere.post.repository.PostRepository;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.sphere.notifications.service.NotificationService;
import com.sphere.common.config.RateLimitService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RateLimitService rateLimitService;

    public CommentResponse createComment(CreateCommentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!rateLimitService.allowComment(email)) {
            throw new RuntimeException("Comment rate limit exceeded. Maximum 30 comments per hour!");
        }

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        if (!post.getAuthor().getEmail().equals(email)) {
            notificationService.notifyComment(
                    post.getAuthor().getUsername(),
                    author.getUsername(),
                    post.getId()
            );
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(author)
                .post(post)
                .parent(parent)
                .build();

        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return mapToResponse(comment);
    }

    public List<CommentResponse> getCommentsByPost(Long postId) {
        List<Comment> topLevel = commentRepository
                .findByPost_IdAndParentIsNullOrderByCreatedAtDesc(postId);

        return topLevel.stream()
                .map(this::mapToResponseWithReplies)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponseWithReplies(Comment comment) {
        List<CommentResponse> replies = commentRepository
                .findByParent_IdOrderByCreatedAtDesc(comment.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        CommentResponse response = mapToResponse(comment);
        response.setReplies(replies);
        return response;
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUsername(comment.getAuthor().getUsername())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .upvotes(comment.getUpvotes())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}