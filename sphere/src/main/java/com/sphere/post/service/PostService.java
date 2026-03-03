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
import com.sphere.post.Vote;
import com.sphere.post.VoteType;
import com.sphere.post.repository.VoteRepository;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public PostResponse vote(Long postId, VoteType voteType) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<Vote> existingVote = voteRepository.findByPost_IdAndUser_Id(postId, user.getId());

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getType() == voteType) {
                // remove vote if same type
                if (voteType == VoteType.UPVOTE) post.setUpvotes(post.getUpvotes() - 1);
                else post.setDownvotes(post.getDownvotes() - 1);
                voteRepository.delete(vote);
            } else {
                // switch vote
                if (voteType == VoteType.UPVOTE) {
                    post.setUpvotes(post.getUpvotes() + 1);
                    post.setDownvotes(post.getDownvotes() - 1);
                } else {
                    post.setDownvotes(post.getDownvotes() + 1);
                    post.setUpvotes(post.getUpvotes() - 1);
                }
                vote.setType(voteType);
                voteRepository.save(vote);
            }
        } else {
            Vote vote = Vote.builder()
                    .user(user)
                    .post(post)
                    .type(voteType)
                    .build();
            voteRepository.save(vote);
            if (voteType == VoteType.UPVOTE) post.setUpvotes(post.getUpvotes() + 1);
            else post.setDownvotes(post.getDownvotes() + 1);
        }

        postRepository.save(post);
        return mapToResponse(post);
    }

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