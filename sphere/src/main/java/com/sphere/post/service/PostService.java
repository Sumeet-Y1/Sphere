package com.sphere.post.service;

import com.sphere.community.Community;
import com.sphere.community.repository.CommunityRepository;
import com.sphere.post.Post;
import com.sphere.post.dto.CreatePostRequest;
import com.sphere.post.dto.PostResponse;
import com.sphere.post.repository.PostRepository;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public PostResponse createPost(CreatePostRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new RuntimeException("Community not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .type(request.getType())
                .author(author)
                .community(community)
                .build();

        postRepository.save(post);
        return mapToResponse(post);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByCommunity(Long communityId) {
        return postRepository.findByCommunity_IdOrderByCreatedAtDesc(communityId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getMyPosts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findByAuthor_IdOrderByCreatedAtDesc(author.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deletePost(Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .linkUrl(post.getLinkUrl())
                .type(post.getType())
                .authorUsername(post.getAuthor().getUsername())
                .communityName(post.getCommunity().getName())
                .upvotes(post.getUpvotes())
                .downvotes(post.getDownvotes())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}