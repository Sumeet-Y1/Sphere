package com.sphere.post.controller;

import com.sphere.post.dto.CreatePostRequest;
import com.sphere.post.dto.PostResponse;
import com.sphere.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sphere.post.VoteType;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PostMapping("/{postId}/vote")
    public ResponseEntity<PostResponse> vote(@PathVariable Long postId, @RequestParam VoteType voteType) {
        return ResponseEntity.ok(postService.vote(postId, voteType));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<PostResponse>> getPostsByCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(postService.getPostsByCommunity(communityId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts() {
        return ResponseEntity.ok(postService.getMyPosts());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}