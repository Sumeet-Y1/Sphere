package com.sphere.community.service;

import com.sphere.community.Community;
import com.sphere.community.dto.CommunityResponse;
import com.sphere.community.dto.CreateCommunityRequest;
import com.sphere.community.repository.CommunityRepository;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public CommunityResponse createCommunity(CreateCommunityRequest request) {
        if (communityRepository.existsByName(request.getName())) {
            throw new RuntimeException("Community name already taken");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Community community = Community.builder()
                .name(request.getName())
                .description(request.getDescription())
                .bannerUrl(request.getBannerUrl())
                .avatarUrl(request.getAvatarUrl())
                .type(request.getType())
                .owner(owner)
                .memberCount(1)
                .build();

        communityRepository.save(community);
        return mapToResponse(community);
    }

    public CommunityResponse getCommunity(String name) {
        Community community = communityRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        return mapToResponse(community);
    }

    public List<CommunityResponse> getAllCommunities() {
        return communityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CommunityResponse> getMyCommunities() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return communityRepository.findByOwner_Id(owner.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommunityResponse mapToResponse(Community community) {
        return CommunityResponse.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .bannerUrl(community.getBannerUrl())
                .avatarUrl(community.getAvatarUrl())
                .type(community.getType())
                .ownerUsername(community.getOwner().getUsername())
                .memberCount(community.getMemberCount())
                .createdAt(community.getCreatedAt())
                .build();
    }
}