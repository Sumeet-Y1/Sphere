package com.sphere.community.controller;

import com.sphere.community.dto.CommunityResponse;
import com.sphere.community.dto.CreateCommunityRequest;
import com.sphere.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<CommunityResponse> createCommunity(@Valid @RequestBody CreateCommunityRequest request) {
        return ResponseEntity.ok(communityService.createCommunity(request));
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> getAllCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/{name}")
    public ResponseEntity<CommunityResponse> getCommunity(@PathVariable String name) {
        return ResponseEntity.ok(communityService.getCommunity(name));
    }

    @GetMapping("/me")
    public ResponseEntity<List<CommunityResponse>> getMyCommunities() {
        return ResponseEntity.ok(communityService.getMyCommunities());
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<CommunityResponse> joinCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityService.joinCommunity(communityId));
    }

    @DeleteMapping("/{communityId}/leave")
    public ResponseEntity<Void> leaveCommunity(@PathVariable Long communityId) {
        communityService.leaveCommunity(communityId);
        return ResponseEntity.noContent().build();
    }
}